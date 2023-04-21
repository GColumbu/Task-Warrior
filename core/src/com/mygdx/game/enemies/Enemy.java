package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.players.garen.Garen;
import com.mygdx.game.players.PlayerChampion;

import java.util.ArrayList;
import java.util.List;

public abstract class Enemy {
    private static final int MIN_SEPARATION_DISTANCE = 50;

    //vectors
    protected Vector2 position;
    protected Vector2 relativePosition;
    protected Vector2 velocity;

    //variables
    protected float maxSpeed;
    protected float maxForce;
    protected float health;
    protected float damage;
    protected boolean isAttacked;

    //minion states
    protected enum State {WALKING, ATTACK};
    protected boolean isInRange = false;
    protected State currentState;
    protected State previousState;
    protected float stateTimer;

    //animations and textures
    protected TextureRegion currentRegion;
    protected TextureRegion idleTextureRegion;
    protected WalkingAnimation walkingAnimation;
    protected WalkingAnimation walkingDamageAnimation;

    //collisions
    protected Rectangle enemyRectangle;

    //constructor
    public Enemy(int x, int y, float maxSpeed, float maxForce, float health, float damage){
        position = new Vector2(x, y);
        relativePosition = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.health = health;
        this.isAttacked = false;
        this.damage = damage;
    }

    public abstract void update(PlayerChampion player, float deltaTime);

    //getters and setters
    public void setCurrentRegion(TextureRegion currentRegion) {
        this.currentRegion = currentRegion;
    }
    protected Vector2 getWalkingRelativePosition() {
        return new Vector2(position.x - walkingAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - walkingAnimation.getKeyFrameHeight(stateTimer) / 2);
    }
    protected TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch(currentState) {
            case ATTACK:
                //TODO:: minion attack animation
            case WALKING:
            default:
                if (isAttacked)
                    region = walkingDamageAnimation.getKeyFrame(stateTimer);
                else
                    region = walkingAnimation.getKeyFrame(stateTimer);
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

    public float getHeading(){
        return velocity.angleDeg();
    }

    public Vector2 getRelativePosition() {
        return relativePosition;
    }

    public void setEnemyRectangle(Rectangle enemyRectangle) {
        this.enemyRectangle = enemyRectangle;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getHealth() {
        return health;
    }

    //orientations
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

    //steering behaviours

    //seek
    protected Vector2 seek(Vector2 target, float deltaTime){
        Vector2 desired = target.sub(this.position);
        desired.setLength(maxSpeed*deltaTime);
        Vector2 steering = desired.sub(velocity);
        steering.limit(deltaTime*maxForce);
        return steering;
    }

    //flee
    protected Vector2 flee(Vector2 target, float deltaTime){
        Vector2 desired = target.sub(this.position);
        desired.setLength(maxSpeed*deltaTime);
        desired.scl(-1);
        Vector2 steering = desired.sub(velocity);
        steering.limit(deltaTime*maxForce);
        return steering;
    }

    //pursue
    protected Vector2 pursue(PlayerChampion player, float deltaTime){
        Vector2 target = player.getPosition().cpy();
        Vector2 velocity = player.getVelocity().cpy();
        float predictionLength = position.dst(target);
        target.add(velocity.scl(predictionLength*deltaTime)); //make prediction based on distance
        return seek(target, deltaTime);
    }

    //evade
    protected Vector2 evade(PlayerChampion player, float deltaTime){
        Vector2 target = player.getPosition().cpy();
        Vector2 velocity = player.getVelocity().cpy();
        float predictionLength = position.dst(target);
        target.add(velocity.scl(predictionLength*deltaTime)); //make prediction based on distance
        return flee(target, deltaTime);
    }

    public void move(PlayerChampion player, List<Enemy> minions, float deltaTime){
        separation(getNearbyEnemies(minions));
        moveAndRecognizeCollision(player, pursue(player, deltaTime));
    }

    //applying steering behaviour
    protected void applySteeringBehaviour(Vector2 steering){
        velocity.add(steering);
        //make enemy recognize walls
        if(position.x > 0 && position.x < TaskWarrior.WIDTH - getSprite().getRegionWidth()) {
            position.x += velocity.x;
        } else if(position.x <= 0 && isLooking("right") || position.x >= TaskWarrior.WIDTH - getSprite().getRegionWidth() && isLooking("left")){
            position.x += velocity.x;
        }

        if(position.y > 0 && position.y < TaskWarrior.HEIGHT - getSprite().getRegionHeight()){
            position.y += velocity.y;
        } else if(position.y <= 0 && isLooking("up") || position.y >= TaskWarrior.HEIGHT - getSprite().getRegionHeight() && isLooking("down")){
            position.y += velocity.y;
        }
    }

    private List<Enemy> getNearbyEnemies(List<Enemy> minions) {
        List<Enemy> nearbyEnemies = new ArrayList<>();
        for (Enemy enemy : minions){ // allEnemies is a list of all enemies in the game
            if (enemy != this && enemy.position.dst(this.position) < MIN_SEPARATION_DISTANCE) {
                nearbyEnemies.add(enemy); // add enemy to the list if it is within the minimum separation distance
            }
        }
        return nearbyEnemies;
    }

    private void separation(List<Enemy> nearbyEnemies) {
        for (Enemy other : nearbyEnemies) {
            if (other != this && this.enemyRectangle.overlaps(other.enemyRectangle)) {
                Vector2 rejection = new Vector2(0, 0);
                // Calculate separation vector
                Vector2 separation = other.position.cpy().sub(position);
                float overlapX = (this.currentRegion.getRegionWidth() + other.currentRegion.getRegionWidth()) / 2 + Math.abs(separation.x);
                float overlapY = (this.currentRegion.getRegionHeight() + other.currentRegion.getRegionHeight()) / 2 + Math.abs(separation.y);
                // Resolve collision
                if (overlapX < overlapY) {
                    rejection.x = overlapX / 2 * Math.signum(separation.x);
                } else {
                    rejection.y = overlapY / 2 * Math.signum(separation.y);
                }
                this.velocity.add(rejection.scl(0.1f));
                other.velocity.sub(rejection.scl(0.1f));
            }
        }
    }
    protected void moveAndRecognizeCollision(PlayerChampion player, Vector2 steeringBehaviour){
        // apply steering behaviour if colision not detected
        if(!enemyRectangle.overlaps(player.getPlayerRectangle())){
            isInRange = false;
            applySteeringBehaviour(steeringBehaviour);
        }else{
            isInRange = true;
            noOverlappingWithPlayer(player);
        }
    }

    // player related behaviours
    protected void calculateDamage(PlayerChampion player) {
        calculateDamageFromMinions(player);
        // minion takes damage from E spell
        if(player.getState() == PlayerChampion.State.E && player.isEAttackTiming(true) && isCollidingWithEnemy(player.getEAttackRange())) {
            deductEnemyHealth(player.getEAttackDamage());
        }
        // minion takes damage from Q spell
        else if (player.getState() == PlayerChampion.State.Q && player.isQAttackTiming(true) && isCollidingWithEnemy(player.getQAttackRange())) {
            deductEnemyHealth(player.getQAttackDamage());
        }
        else if (player.getState() == PlayerChampion.State.W && player.isWAttackTiming(true) && isCollidingWithEnemy(player.getWAttackRange())) {
            deductEnemyHealth(player.getWAttackDamage());
        }
        //minion doesn't take damage
        else {
            isAttacked = false;
        }
    }

    // transforms Rectangle to Polygon
    private Polygon rectToPolygon(Rectangle r) {
        Polygon rPoly = new Polygon(new float[] { 0, 0, r.width, 0, r.width,
                r.height, 0, r.height });
        rPoly.setPosition(r.x, r.y);
        return rPoly;
    }

    // check if Polygon intersects Rectangle
    private boolean isCollision(Polygon p, Rectangle r) {
        Polygon rPoly = rectToPolygon(r);
        return Intersector.overlapConvexPolygons(rPoly, p);
    }

    //check if Circle intersects Rectangle
    private boolean isCollision(Circle circ, Rectangle rect){
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

    // don't allow player texture to go over the minion texture
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

    private void calculateDamageFromMinions(PlayerChampion player){
        if(getState() == State.ATTACK){
            player.decrementHealth(damage);
        }
    }

    private void deductEnemyHealth(float damage){
        health -= damage;
        isAttacked = true;
        //debug
        System.out.println(health);
    }

    private boolean isCollidingWithEnemy(Shape2D attackRange){
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

}
