package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.players.PlayerChampion;

public abstract class Enemy {

    protected Vector2 position;

    protected int health;
    protected Vector2 relativePosition;
    protected Vector2 velocity;
    protected float maxSpeed;
    protected float maxForce;
    protected boolean isAttacked = false;

    protected Rectangle enemyRectangle;

    //constructor
    public Enemy(int x, int y, float maxSpeed, float maxForce, int health){
        position = new Vector2(x, y);
        relativePosition = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        this.health = health;
    }

    public float getHeading(){
        return velocity.angleDeg();
    }

    public Vector2 getRelativePosition() {
        return relativePosition;
    }

    //update the position and velocity
    public abstract void update(PlayerChampion player, float deltaTime);

    //getters and setters
    public Vector2 getPosition() {
        return position;
    }

    public abstract TextureRegion getSprite();

    public Rectangle getEnemyRectangle() {
        return enemyRectangle;
    }

    public void setEnemyRectangle(Rectangle enemyRectangle) {
        this.enemyRectangle = enemyRectangle;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public int getHealth() {
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

    // player related behaviours
    protected void calculateDamage(PlayerChampion player) {
        // minion takes damage from E spell
        if(player.getState() == PlayerChampion.State.E && isCollision(player.getEAttackRange(), enemyRectangle)) {
            deductEnemyHealth(player.getEAttackDamage());
        }
        // minion takes damage from Q spell
        else if(player.getState() == PlayerChampion.State.Q){
            // first slash (return Rectangle)
            if(player.getQAttackRange() instanceof Rectangle){
                Rectangle rectangle = (Rectangle)player.getQAttackRange();
                if(rectangle.overlaps(enemyRectangle))
                    deductEnemyHealth(player.getQAttackDamage());
            }
            //second slash (return Circle)
            else if(player.getQAttackRange() instanceof Circle){
                Circle circle = (Circle)player.getQAttackRange();
                if(isCollision(circle, enemyRectangle))
                    deductEnemyHealth(player.getQAttackDamage());
            }
            //third slash (return Polygon)
            else if(player.getQAttackRange() instanceof Polygon){
                Polygon polygon = (Polygon)player.getQAttackRange();
                if(isCollision(polygon, enemyRectangle))
                    deductEnemyHealth(player.getQAttackDamage());
            }
        }
        // minion takes damage from W spell
        else if(player.getState() == PlayerChampion.State.W && player.getW_Animation().isBurst(player.getStateTimer(), true) && isCollision(player.getWAttackRange(), enemyRectangle)){
            deductEnemyHealth(player.getWAttackDamage());
        }
        //minion doesn't take damage
        else {
            isAttacked = false;
        }
    }

    private void deductEnemyHealth(int damage){
        health -= damage;
        isAttacked = true;
        //debug
        System.out.println(health);
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

}
