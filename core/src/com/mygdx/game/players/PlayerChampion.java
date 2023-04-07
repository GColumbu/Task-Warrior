package com.mygdx.game.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.TaskWarrior;

public abstract class PlayerChampion {

    //vectors
    protected Vector2 position;
    protected Vector2 relativePosition;
    protected Vector2 velocity;
    protected Vector2 previousMovingVelocity;

    // variables
    protected float previousX;
    protected float previousY;
    protected int speed;
    protected float health;

    //player states
    public enum State {STANDING, WALKING, Q, E, W}
    protected State currentState;
    protected State previousState;
    protected float stateTimer;

    //textures
    protected TextureRegion currentRegion;
    protected TextureRegion idleTextureRegion;
    protected WalkingAnimation walkingAnimation;

    //collisions
    protected Rectangle playerRectangle;

    //constructor
    public PlayerChampion(int x, int y, int speed, int health){
        position = new Vector2(x, y);
        relativePosition = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        previousMovingVelocity = new Vector2(0, 0);
        this.speed = speed;
        this.health = health;
    }

    public abstract void update(float deltaTime);

    //setters
    public void setCurrentRegion(TextureRegion currentRegion) {
        this.currentRegion = currentRegion;
    }
    public void setPlayerRectangle(Rectangle playerRectangle) {
        this.playerRectangle = playerRectangle;
    }
        // used not to let the player advance when hitting a minion
    public void setPositionX(float position) {
        this.position.x = position;
    }
    public void setPositionY(float position) {
        this.position.y = position;
    }

    // calculates where the draw function should start
    protected void setRelativePosition() {
        currentState = getState();
        switch (currentState) {
            case WALKING:
            case STANDING:
                relativePosition = getIdleRelativePosition();
                break;
        }
    }

    //getters
    public float getHealth() {
        return health;
    }

    public void decrementHealth(float damage){
        health -= damage;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public TextureRegion getSprite() {
        return currentRegion;
    }

    public Vector2 getRelativePosition() {
        return relativePosition;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getPreviousX() {
        return previousX;
    }
    public float getPreviousY() {
        return previousY;
    }

    public Rectangle getPlayerRectangle() {
        return playerRectangle;
    }

    // get player angle/orientation
    public float getHeading(){
        if(!velocity.equals(Vector2.Zero))
            return velocity.angleDeg();
        //if player not moving use the last velocity to keep the player oriented in the last direction
        return previousMovingVelocity.angleDeg();
    }

    protected TextureRegion getIdleTextureRegion() {return idleTextureRegion;}

    protected Vector2 getIdleRelativePosition() {
        return new Vector2(position.x - idleTextureRegion.getRegionWidth() / 2, position.y - idleTextureRegion.getRegionHeight() / 2);
    }

    // movement function
    protected void movePlayer(float deltaTime){
        // reset velocity vector
        velocity = new Vector2(0, 0);
        // movement on y axis
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (getIdleRelativePosition().y < TaskWarrior.HEIGHT - idleTextureRegion.getRegionHeight()) {
                velocity.y = deltaTime * speed;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (getIdleRelativePosition().y > 0) {
                velocity.y = -deltaTime * speed;
            }
        }
        // movement on x axis
         if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (getIdleRelativePosition().x > 0) {
                velocity.x = -deltaTime * speed;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
             if (getIdleRelativePosition().x < TaskWarrior.WIDTH - idleTextureRegion.getRegionWidth()) {
                 velocity.x = deltaTime * speed;
             }
         }

        // if player is not moving record the last moving velocity
        if(!velocity.equals(Vector2.Zero)){
            previousMovingVelocity = velocity;
        }

        // record previous position to check for collision towards the minion
        previousX = position.x;
        previousY = position.y;

        // add velocity to position vector
        position.add(velocity);
    }

    // gets the player texture frame based on the current state
    protected TextureRegion getCurrentFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case WALKING:
                region = walkingAnimation.getKeyFrame(stateTimer);
                break;
            default:
            case STANDING:
                region = idleTextureRegion;
                break;
        }
        if (previousState == currentState){
            stateTimer += deltaTime;
        } else
            stateTimer = 0;
        previousState = currentState;
        return region;
    }

    public State getState(){
        //WALKING checking state
        if(isMoving()){
            return State.WALKING;
        }
        //IDLE checking state
        return State.STANDING;
    }

    //update state functions

    protected boolean isMoving(){
        return (Gdx.input.isKeyPressed(Input.Keys.UP)) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                || (Gdx.input.isKeyPressed(Input.Keys.LEFT)) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
    }

    protected boolean areAnimationsOngoing(State state){
        switch(state){
            case W:
                return currentState != State.Q && currentState != State.E;
            case Q:
                return currentState != State.E && currentState != State.W;
            case E:
                return currentState != State.Q && currentState != State.W;
        }
        return true;
    }
}
