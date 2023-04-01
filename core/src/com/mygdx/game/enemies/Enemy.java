package com.mygdx.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
        if(player.getState() == PlayerChampion.State.E_SPIN && player.getEAttackRange().overlaps(enemyRectangle)) {
            health -= player.getEAttackDamage();
            isAttacked = true;
            //debug
            System.out.println(health);
        } else if(player.getState() == PlayerChampion.State.Q && player.getWAttackRange().overlaps(enemyRectangle)){
            health -= player.getQAttackDamage();
            isAttacked = true;
            //debug
            System.out.println(health);
        } else if(player.getState() == PlayerChampion.State.W && player.getStateTimer() > 0.07f * 6 && player.getWAttackRange().overlaps(enemyRectangle)){
            health -= player.getWAttackDamage();
            isAttacked = true;
            //debug
            System.out.println(health);
        } else {
            isAttacked = false;
        }
    }

    protected void noOverlapping(PlayerChampion player) {
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
