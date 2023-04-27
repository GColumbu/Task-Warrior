package com.mygdx.game.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.TaskWarrior;

public abstract class PlayerChampion {

    // vectors
    protected Vector2 position;
    protected Vector2 relativePosition;
    protected Vector2 velocity;
    protected Vector2 previousMovingVelocity;

    // variables
    protected float previousX;
    protected float previousY;
    protected int speed;
    protected float health;

    // player states
    public enum State {STANDING, WALKING, Q, E, W}
    protected State currentState;
    protected State previousState;
    protected float stateTimer;

    // textures and animations
    protected TextureRegion currentRegion;
    protected TextureRegion idleTextureRegion;
    protected WalkingAnimation walkingAnimation;
    protected AttackAnimation qBasicAnimation;
    protected AttackAnimation wBasicAnimation;
    protected AttackAnimation eBasicAnimation;

    // collisions
    protected Rectangle playerRectangle;
    protected Circle forbiddenMinionSpawnRange;
    protected Circle runnerBehaviorRange;

    // constructor
    public PlayerChampion(int x, int y, int speed, float health){
        position = new Vector2(x, y);
        relativePosition = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        previousMovingVelocity = new Vector2(0, 0);
        this.speed = speed;
        this.health = health;
    }

    public abstract void update(float deltaTime);

    // setters
    public void setCurrentRegion(TextureRegion currentRegion) {
        this.currentRegion = currentRegion;
    }
    public void setPlayerRectangle(Rectangle playerRectangle) {
        this.playerRectangle = playerRectangle;
    }
    public void setForbiddenMinionSpawnRange(Circle forbiddenMinionSpawnRange) {
        this.forbiddenMinionSpawnRange = forbiddenMinionSpawnRange;
    }
    public void setRunnerBehaviorRange(Circle runnerBehaviorRange) {
        this.runnerBehaviorRange = runnerBehaviorRange;
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

    // q ability methods
    public AttackAnimation getQBasicAnimation() { return qBasicAnimation;}
    public abstract Shape2D getQAttackRange();
    public abstract float getQAttackDamage();
    public abstract boolean isQAttackTiming(boolean forCollision);


    // W ability methods
    public AttackAnimation getWBasicAnimation() { return wBasicAnimation;}
    public abstract Shape2D getWAttackRange();
    public abstract float getWAttackDamage();
    public abstract boolean isWAttackTiming(boolean forCollision);


    // E ability methods
    public AttackAnimation getEBasicAnimation() {
        return eBasicAnimation;
    }
    public abstract Shape2D getEAttackRange();
    public abstract float getEAttackDamage();
    public abstract boolean isEAttackTiming(boolean forCollision);

    // getters
    public void decrementHealth(float damage){
        health -= damage;
    }
    public void incrementHealth(float healing){
        if(health != getMaxHealth())
        health += healing;
    }
    public abstract float getMaxHealth();
    public float getHealth() {
        return health;
    }
    public Vector2 getPosition() {
        return position;
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
    public float getStateTimer() {
        return stateTimer;
    }
    public Rectangle getPlayerRectangle() {
        return playerRectangle;
    }
    public Circle getForbiddenMinionSpawnRange() {
        return forbiddenMinionSpawnRange;
    }
    public Circle getRunnerBehaviorRange() {
        return runnerBehaviorRange;
    }
    // get player angle/orientation
    public float getHeading(){
        if(!velocity.equals(Vector2.Zero))
            return velocity.angleDeg();
        // if player not moving use the last velocity to keep the player oriented in the last direction
        return previousMovingVelocity.angleDeg();
    }
    // gets the player current texture frame based on the current state
    protected abstract TextureRegion getCurrentFrame(float deltaTime);
    protected TextureRegion getIdleTextureRegion() {return idleTextureRegion;}
    protected Vector2 getIdleRelativePosition() {
        return new Vector2(position.x - idleTextureRegion.getRegionWidth() / 2, position.y - idleTextureRegion.getRegionHeight() / 2);
    }

    // MOVEMENT METHODS
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

    // gets current state based on player decisions
    public State getState() {

        // Q checking state
        if ((Gdx.input.isKeyPressed(Input.Keys.Q)) && areAnimationsNotOngoingAndCooldownFinished(State.Q)) {
            // reset q cooldown
            if ((Gdx.input.isKeyPressed(Input.Keys.Q))){
                qBasicAnimation.resetCooldown();
            }
            return State.Q;
        }
        if(!qBasicAnimation.isAnimationFinished(stateTimer) && previousState == State.Q){
            // cancel q
            if(Gdx.input.isKeyJustPressed(Input.Keys.Q)){
                return State.STANDING;
            }
            return State.Q;
        }

        // W checking state
        if ((Gdx.input.isKeyJustPressed(Input.Keys.W)) && areAnimationsNotOngoingAndCooldownFinished(State.W)) {
            // reset w cooldown
            if ((Gdx.input.isKeyJustPressed(Input.Keys.W))){
                wBasicAnimation.resetCooldown();
            }
            return State.W;
        }
        if(!wBasicAnimation.isAnimationFinished(stateTimer) && previousState == State.W){
            // cancel w
            if(Gdx.input.isKeyJustPressed(Input.Keys.W)){
                return State.STANDING;
            }
            return State.W;
        }

        // E checking state
        if ((Gdx.input.isKeyPressed(Input.Keys.E)) && areAnimationsNotOngoingAndCooldownFinished(State.E)){
            // reset e cooldown
            if ((Gdx.input.isKeyPressed(Input.Keys.E))){
                eBasicAnimation.resetCooldown();
            }
            return State.E;
        }
        if(!eBasicAnimation.isAnimationFinished(stateTimer) && previousState == State.E){
            // cancel e
            if(Gdx.input.isKeyJustPressed(Input.Keys.E)){
                return State.STANDING;
            }
            return State.E;
        }

        // WALKING checking state
        if(isMoving()){
            return State.WALKING;
        }

        // IDLE checking state
        return State.STANDING;
    }

    // COOLDOWN/ANIMATION METHODS

    public void updateCooldowns(float deltaTime){
        // add delta time to ability stateTimer
        if(qBasicAnimation.cooldownStateTimer < qBasicAnimation.cooldownDuration)
            qBasicAnimation.cooldownStateTimer += deltaTime;
        if(wBasicAnimation.cooldownStateTimer < wBasicAnimation.cooldownDuration)
            wBasicAnimation.cooldownStateTimer += deltaTime;
        if(eBasicAnimation.cooldownStateTimer < eBasicAnimation.cooldownDuration)
            eBasicAnimation.cooldownStateTimer += deltaTime;
    }

    protected boolean isMoving(){
        return (Gdx.input.isKeyPressed(Input.Keys.UP)) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                || (Gdx.input.isKeyPressed(Input.Keys.LEFT)) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
    }

    private boolean areAnimationsNotOngoingAndCooldownFinished(State state){
        switch(state){
            case W:
                return currentState != State.Q && currentState != State.E && wBasicAnimation.cooldownStateTimer >= wBasicAnimation.cooldownDuration;
            case Q:
                return currentState != State.E && currentState != State.W && qBasicAnimation.cooldownStateTimer >= qBasicAnimation.cooldownDuration;
            case E:
                return currentState != State.Q && currentState != State.W && eBasicAnimation.cooldownStateTimer >= eBasicAnimation.cooldownDuration;
        }
        return true;
    }
}
