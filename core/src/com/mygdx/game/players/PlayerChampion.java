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
    protected float speed;

        // health and armor variables
    protected float health;
    protected float maxHealth;
    protected float armor;
    protected float maxArmor;
    protected float armorEffectiveness;
    protected float armorStateTimer;
    protected float armorDuration;
    protected float armorIncreaseRate;

    // player states
    public enum State {STANDING, WALKING, DEATH, Q, E, W}
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
    public PlayerChampion(int x, int y, float speed, float maxHealth, float maxArmor, float armorEffectiveness, float armorDuration, float armorIncreaseRate){
        position = new Vector2(x, y);
        relativePosition = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        previousMovingVelocity = new Vector2(0, 0);
        this.speed = speed;
        this.maxHealth = maxHealth;
        this.health = this.maxHealth;
        this.maxArmor = maxArmor;
        this.armor = this.maxArmor;
        this.armorEffectiveness = armorEffectiveness;
        this.armorStateTimer = 0;
        this.armorDuration = armorDuration;
        this.armorIncreaseRate = armorIncreaseRate;
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

    // player death methods
    public abstract boolean isDeathAnimationFinished();

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
    public float getMaxHealth(){return maxHealth; }
    public float getMaxArmor(){return maxArmor; }
    public State getCurrentState(){return currentState; }
    public float getHealth() {
        return health;
    }
    public float getArmor() {
        return armor;
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
        return new Vector2(position.x - idleTextureRegion.getRegionWidth() / 2.0f, position.y - idleTextureRegion.getRegionHeight() / 2.0f);
    }

    // MOVEMENT METHODS
    protected void movePlayer(float deltaTime){
        // reset velocity vector
        velocity = new Vector2(0, 0);

        Vector2 idleRelativePosition = getIdleRelativePosition();
        int constantSpeed = (int)(deltaTime * speed);

        // movement on y axis
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (idleRelativePosition.y < TaskWarrior.HEIGHT - idleTextureRegion.getRegionHeight()) {
                velocity.y = constantSpeed;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (idleRelativePosition.y > 0) {
                velocity.y = -constantSpeed;
            }
        }
        // movement on x axis
         if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (idleRelativePosition.x > 0) {
                velocity.x = -constantSpeed;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
             if (idleRelativePosition.x < TaskWarrior.WIDTH - idleTextureRegion.getRegionWidth()) {
                 velocity.x = constantSpeed;
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
        // checks if player died
        if(health <= 0){
            return State.DEATH;
        }

        // Q checking state
        boolean isQKeyPressed = Gdx.input.isKeyJustPressed(Input.Keys.Q);
        if (isQKeyPressed && areAnimationsNotOngoingAndCooldownFinished(State.Q)) {
            // reset q cooldown
            qBasicAnimation.resetCooldown();
            return State.Q;
        }
        if(!qBasicAnimation.isAnimationFinished(stateTimer) && previousState == State.Q){
            // cancel q
            if(isQKeyPressed){
                return State.STANDING;
            }
            return State.Q;
        }

        // W checking state
        boolean isWKeyPressed = Gdx.input.isKeyJustPressed(Input.Keys.W);
        if (isWKeyPressed && areAnimationsNotOngoingAndCooldownFinished(State.W)) {
            // reset w cooldown
            wBasicAnimation.resetCooldown();
            return State.W;
        }
        if(!wBasicAnimation.isAnimationFinished(stateTimer) && previousState == State.W){
            // cancel w
            if(isWKeyPressed){
                return State.STANDING;
            }
            return State.W;
        }

        // E checking state
        boolean isEKeyPressed = Gdx.input.isKeyJustPressed(Input.Keys.E);
        if (isEKeyPressed && areAnimationsNotOngoingAndCooldownFinished(State.E)){
            // reset e cooldown
            eBasicAnimation.resetCooldown();
            return State.E;
        }
        if(!eBasicAnimation.isAnimationFinished(stateTimer) && previousState == State.E){
            // cancel e
            if(isEKeyPressed){
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

    // HEALTH/ARMOR METHODS
    public void incrementHealth(float healing){
        if(health != maxHealth)
            health += healing;
    }

    public void incrementArmor(float healing){
        if(armor != maxArmor)
            armor += healing;
    }

    public void calculateDamage(float totalDamage){
        if (armor <= 0){
            health -= totalDamage;
        } else {
            float armorDamage = (armorEffectiveness / 100) * totalDamage;
            float healthDamage = totalDamage - armorDamage;
            armor -= armorDamage;
            health -= healthDamage;
        }
    }

    protected void updateArmor(float deltaTime){
        // update "armorStateTimer" based on if the player is taking damage or not
        if(armorStateTimer < armorDuration)
            armorStateTimer += deltaTime;

        // increase armor if player didn't take damage for more than "armorDuration" seconds
        if(armorStateTimer >= armorDuration && armor < maxArmor){
            armor += armorIncreaseRate;
        }
    }

    public void resetArmorStateTimer(){
        armorStateTimer = 0;
    }

}
