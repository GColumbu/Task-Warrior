package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.players.PlayerChampion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Enemy {
    // vectors
    protected Vector2 position;
    protected Vector2 relativePosition;
    protected Vector2 velocity;
    protected Vector2 separation;
    protected Vector2 currentSteeringBehavior;

    // variables
    protected float maxSpeed;
    protected float maxForce;
    protected float health;
    protected float damage;
    protected boolean isAttacked;
    protected float wanderAngle;

    // minion states
    protected enum State {WALKING, ATTACK, DEAD};
    protected boolean isInRange = false;
    protected State currentState;
    protected State previousState;
    protected float stateTimer;

    // animations and textures
    protected TextureRegion currentRegion;
    protected TextureRegion idleTextureRegion;
    protected EnemyAnimation walkingAnimation;
    protected EnemyAnimation walkingDamageAnimation;
    protected EnemyAnimation attackAnimation;
    protected EnemyAnimation attackDamageAnimation;
    protected EnemyAnimation dyingAnimation;

    // collisions
    protected Rectangle enemyRectangle;
    protected Circle minionSenseRange;

    // constructor
    public Enemy(int x, int y, float maxSpeed, float maxForce, float health, float damage){
        position = new Vector2(x, y);
        relativePosition = new Vector2(x, y);
        velocity = new Vector2(1, 0);
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.health = health;
        this.isAttacked = false;
        this.damage = damage;
        this.wanderAngle = 0f;
    }

    public abstract void update(PlayerChampion player, float deltaTime);

    // GETTERS AND SETTERS
    public void setCurrentRegion(TextureRegion currentRegion) {
        this.currentRegion = currentRegion;
    }
    protected Vector2 getWalkingRelativePosition() {
        return new Vector2(position.x - walkingAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - walkingAnimation.getKeyFrameHeight(stateTimer) / 2);
    }
    protected Vector2 getAttackRelativePosition() {
        return new Vector2(position.x - attackAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - attackAnimation.getKeyFrameHeight(stateTimer) / 2);
    }
    protected Vector2 getDeadRelativePosition() {
        return new Vector2(position.x - dyingAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - dyingAnimation.getKeyFrameHeight(stateTimer) / 2);
    }
    protected TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch(currentState) {
            case ATTACK:
                if (isAttacked)
                    region = attackDamageAnimation.getKeyFrame(stateTimer, true);
                else
                    region = attackAnimation.getKeyFrame(stateTimer, true);
                relativePosition = getAttackRelativePosition();
                break;
            case DEAD:
                region = dyingAnimation.getKeyFrame(stateTimer, false);
                relativePosition = getDeadRelativePosition();
                break;
            case WALKING:
            default:
                if (isAttacked)
                    region = walkingDamageAnimation.getKeyFrame(stateTimer, true);
                else
                    region = walkingAnimation.getKeyFrame(stateTimer, true);
                relativePosition = getWalkingRelativePosition();
                break;
        }
        if (previousState == currentState){
            stateTimer += deltaTime;
        } else
            stateTimer = 0;
        previousState = currentState;
        return region;
    }
    private State getState(){
        if(health <= 0){
            return State.DEAD;
        }
        if(isInRange){
            return State.ATTACK;
        }
        relativePosition = getWalkingRelativePosition();
        return State.WALKING;
    }
    public Vector2 getPosition() {
        return position;
    }
    public TextureRegion getSprite(){
        return currentRegion;
    }
    public Rectangle getEnemyRectangle() {
        return enemyRectangle;
    }
    public Circle getMinionSenseRange() {
        return minionSenseRange;
    }
    public float getHeading(){
        return velocity.angleDeg();
    }
    public Vector2 getRelativePosition() {
        return relativePosition;
    }
    public void setEnemyRectangle(Rectangle enemyRectangle) {
        this.enemyRectangle = enemyRectangle;
    }
    public void setMinionSenseRange(Circle minionSenseRange) {
        this.minionSenseRange = minionSenseRange;
    }

    // orientation
     private boolean isLooking(String direction){
        if (direction.equals("right"))
            return this.getHeading() < 90 && this.getHeading() > 0 || this.getHeading() > 270 && this.getHeading() < 360 || this.getHeading()==0;
        else if (direction.equals("left"))
            return this.getHeading() > 90 && this.getHeading() < 270;
        else if (direction.equals("up"))
            return this.getHeading() > 0 && this.getHeading() < 180;
        else if (direction.equals("down"))
            return this.getHeading() > 180 && this.getHeading() < 360;
        return false;
     }

    // STEERING BEHAVIORS

    // seek
    protected Vector2 seek(Vector2 target, float deltaTime){
        Vector2 desired = target.sub(this.position);
        desired.setLength(maxSpeed*deltaTime);
        Vector2 steering = desired.sub(velocity);
        steering.limit(deltaTime*maxForce);
        return steering;
    }

    // flee
    protected Vector2 flee(Vector2 target, float deltaTime){
        Vector2 desired = target.sub(this.position);
        desired.setLength(maxSpeed*deltaTime);
        desired.scl(-1);
        Vector2 steering = desired.sub(velocity);
        steering.limit(deltaTime*maxForce);
        return steering;
    }

    // pursue
    protected Vector2 pursue(PlayerChampion player, float deltaTime){
        Vector2 target = player.getPosition().cpy();
        Vector2 velocity = player.getVelocity().cpy();
        float predictionLength = position.dst(target);
        target.add(velocity.scl(predictionLength*deltaTime)); //make prediction based on distance
        return seek(target, deltaTime);
    }

    // evade
    protected Vector2 evade(PlayerChampion player, float deltaTime){
        Vector2 target = player.getPosition().cpy();
        Vector2 velocity = player.getVelocity().cpy();
        float predictionLength = position.dst(target);
        target.add(velocity.scl(predictionLength*deltaTime)); //make prediction based on distance
        return flee(target, deltaTime);
    }

    // wander
        // get random number in range
    private float getRandom(float range){
        return (float)(Math.random() * 2 * range) - range;
    }
    protected Vector2 wander(float deltaTime){
        int wanderRadius = 50;
        double theta = Math.toRadians(wanderAngle) + Math.toRadians(velocity.angleDeg());
        //project the velocity
        Vector2 wanderPoint = velocity.cpy();
        wanderPoint.setLength(100);
        wanderPoint.add(position);

        // calculate coordinates constrained in the wanderRadius radius circle
        float x = wanderRadius * (float)Math.cos(theta);
        float y = wanderRadius * (float)Math.sin(theta);
        wanderPoint.add(x, y);

        // calculate the steering force
        Vector2 steering = wanderPoint.sub(position);
        steering.setLength(deltaTime* maxForce);

        // adding to the wander angle
        wanderAngle += getRandom(15);
        return steering;
    }

    // separation
        // get nearby enemies
    protected List<Enemy> getNearbyEnemies(List<Enemy> minions, boolean onlyRunners) {
        List<Enemy> nearbyEnemies = new ArrayList<>();
        for (Enemy enemy : minions){ // allEnemies is a list of all enemies in the game
            if(onlyRunners)
                minionSenseRange.radius = 200;
            if (enemy != this && minionSenseRange.contains(enemy.position)) {
                // add enemy to the list if it is within the minimum separation distance
                if(onlyRunners && enemy instanceof Runner)
                    nearbyEnemies.add(enemy);
                else if (!onlyRunners)
                    nearbyEnemies.add(enemy);
            }
            minionSenseRange.radius = 100;
        }
        return nearbyEnemies;
    }
        // add separation vectors for nearby enemies
    protected void separation(List<Enemy> nearbyEnemies) {
        separation = new Vector2(0,0);
        for (Enemy other : nearbyEnemies) {
            if (other != this) {
                // Calculate separation vector
                Vector2 rejection = position.cpy().sub(other.position);
                rejection.nor();
                rejection.scl(2f);
                separation.add(rejection);
            }
        }
    }

    // MOVEMENT METHODS
    // move method called in update AFTER all the enemyRectangles are set
    public abstract void move(PlayerChampion player, List<Enemy> minions, float deltaTime);
    protected abstract void addBehavior(PlayerChampion player, float deltaTime);
    // applying steering behaviour
    protected void applySteeringBehaviour(Vector2 steering, float deltaTime){
        velocity.add(steering);
        velocity.limit(maxSpeed * deltaTime);
        velocity.add(separation);
        applyVelocityAndRecognizeWalls(velocity);
        //subtract the separation in order for the heading to be steady
        velocity.sub(separation);
    }

    // make enemy recognize walls for steering behaviors
    protected void applyVelocityAndRecognizeWalls(Vector2 velocity){
        if(relativePosition.x > 0 && relativePosition.x < TaskWarrior.WIDTH - getSprite().getRegionWidth()) {
            position.x += velocity.x;
        } else if(relativePosition.x <= 0 && isLooking("right") || relativePosition.x >= TaskWarrior.WIDTH - getSprite().getRegionWidth() && isLooking("left")){
            position.x += velocity.x;
        }

        if(relativePosition.y > 0 && relativePosition.y < TaskWarrior.HEIGHT - getSprite().getRegionHeight()){
            position.y += velocity.y;
        } else if(relativePosition.y <= 0 && isLooking("up") || relativePosition.y >= TaskWarrior.HEIGHT - getSprite().getRegionHeight() && isLooking("down")){
            position.y += velocity.y;
        }
    }

    // don't allow player texture to go over the minion texture and push minion
    protected void noOverlappingWithPlayer(PlayerChampion player) {
        float xDifference = Math.abs(player.getPosition().x - position.x);
        float xDifferencePrevious = Math.abs(player.getPreviousX() - position.x);

        float yDifference = Math.abs(player.getPosition().y - position.y);
        float yDifferencePrevious = Math.abs(player.getPreviousY() - position.y);

        if(xDifference < xDifferencePrevious){
            player.setPositionX(player.getPreviousX());
        }

        if(yDifference < yDifferencePrevious){
            player.setPositionY(player.getPreviousY());
        }
    }

    // DAMAGE METHODS
    protected void calculateDamage(PlayerChampion player, int damageFrame) {
        calculateDamageFromMinions(player, damageFrame);
        calculateDamageToMinions(player);
    }

    private void calculateDamageFromMinions(PlayerChampion player, int damageFrame){
        if(currentState == State.ATTACK && attackAnimation.getKeyFrameIndex(stateTimer) == damageFrame){
            player.decrementHealth(damage);
        }
    }

    private void calculateDamageToMinions(PlayerChampion player){
        // minion takes damage from E spell
        if(player.getState() == PlayerChampion.State.E && player.isEAttackTiming(true) && isCollidingWithEnemyAttackRange(player.getEAttackRange())) {
            deductEnemyHealth(player.getEAttackDamage());
        }
        // minion takes damage from Q spell
        else if (player.getState() == PlayerChampion.State.Q && player.isQAttackTiming(true) && isCollidingWithEnemyAttackRange(player.getQAttackRange())) {
            deductEnemyHealth(player.getQAttackDamage());
        }
        else if (player.getState() == PlayerChampion.State.W && player.isWAttackTiming(true) && isCollidingWithEnemyAttackRange(player.getWAttackRange())) {
            deductEnemyHealth(player.getWAttackDamage());
        }
        // minion doesn't take damage
        else {
            isAttacked = false;
        }
    }

    private void deductEnemyHealth(float damage){
        health -= damage;
        isAttacked = true;
    }

    // COLLISION METHODS

    // transforms Rectangle to Polygon
    private static Polygon rectToPolygon(Rectangle r) {
        Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width,
                r.height, 0, r.height });
        rPoly.setPosition(r.x, r.y);
        return rPoly;
    }

    private boolean isCollidingWithEnemyAttackRange(Shape2D attackRange){
        // attack range is Rectangle
        if(attackRange instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) attackRange;
            return rectangle.overlaps(enemyRectangle);
        }
        // attack range is Polygon
        else if(attackRange instanceof Polygon) {
            Polygon polygon = (Polygon) attackRange;
            return isCollision(polygon, enemyRectangle);

        }
        // attack range is circle
        else if(attackRange instanceof Circle) {
            Circle circle = (Circle) attackRange;
            return isCollision(circle, enemyRectangle);
        }
        return false;
    }

    // check if Polygon intersects Rectangle
    protected boolean isCollision(Polygon p, Rectangle r) {
        Polygon rPoly = rectToPolygon(r);
        return Intersector.overlapConvexPolygons(rPoly, p);
    }

    // check if Circle intersects Rectangle
    protected static boolean isCollision(Circle circ, Rectangle rect) {
        Polygon p = rectToPolygon(rect);
        float[] vertices = p.getTransformedVertices();
        Vector2 center = new Vector2(circ.x, circ.y);
        float squareRadius = circ.radius * circ.radius;
        for (int i = 0; i < vertices.length; i += 2) {
            if (i == 0) {
                if (Intersector.intersectSegmentCircle(new Vector2(
                        vertices[vertices.length - 2],
                        vertices[vertices.length - 1]), new Vector2(
                        vertices[i], vertices[i + 1]), center, squareRadius))
                    return true;
            } else {
                if (Intersector.intersectSegmentCircle(new Vector2(
                        vertices[i - 2], vertices[i - 1]), new Vector2(
                        vertices[i], vertices[i + 1]), center, squareRadius))
                    return true;
            }
        }
        return false;
    }

    // methods used in order to remove minion from array
    public boolean isDyingAnimationFinished() {
        return currentState == State.DEAD && dyingAnimation.animation.isAnimationFinished(stateTimer);
    }

}
