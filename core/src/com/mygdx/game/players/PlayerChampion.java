package com.mygdx.game.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.TaskWarrior;

public abstract class PlayerChampion {

    //vectors
    protected Vector2 position;

    protected Vector2 relativePosition;
    protected Vector2 velocity;

    protected float previousX;
    float previousY;
    protected int speed;
    protected Vector2 previousMovingVelocity = new Vector2(0, 0);

    //player states
    public enum State {STANDING, WALKING, Q, E_GRAB, E_SPIN, W, W_WALKING, W_IDLE}
    public State currentState;
    public State previousState;
    protected float stateTimer;

    //animations and textures
    protected TextureRegion currentRegion;
    protected TextureRegion idleTextureRegion;

    //walking
    protected Animation<TextureRegion> walkingAnimation;
    protected Texture walkingTexture;

    // w animation

        //BURST
    protected Animation<TextureRegion> wBurstAnimation;
    protected Texture wBurstTexture;
        //WALKING
    protected Animation<TextureRegion> wWalkingAnimation;
    protected Texture wWalkingTexture;
    protected boolean isWAnimationFinished = true;
        //IDLE
    protected TextureRegion idleInvincibilityTextureRegion;

    // q slash animation

    protected Animation<TextureRegion> qSlashAnimation;
    protected Texture qSlashTexture;
    protected boolean isQAnimationFinished = true;

    // e spin animation

    protected Texture eSpinTexture;

        // sword grap

    protected Animation<TextureRegion> eSwordGrabAnimation;
    protected Animation<TextureRegion> eSpinAnimation;
    protected boolean isEAnimationFinished = true;




    //collisions
    protected Rectangle playerRectangle;




    //constructor
    public PlayerChampion(int x, int y, int speed){
        position = new Vector2(x, y);
        relativePosition = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        this.speed = speed;
    }

    public abstract void update(float deltaTime);

    //getters
    public Vector2 getPosition() {
        return position;
    }

    protected Vector2 getIdleRelativePosition() {
        return new Vector2(position.x - idleTextureRegion.getRegionWidth() / 2, position.y - idleTextureRegion.getRegionHeight() / 2);
    }

    protected Vector2 getWBurstRelativePosition() {
        return new Vector2(position.x - wBurstAnimation.getKeyFrame(stateTimer, false).getRegionWidth() / 2, position.y - wBurstAnimation.getKeyFrame(stateTimer, false).getRegionHeight() / 2);
    }

    protected Vector2 getQSlashRelativePosition() {
        return new Vector2(position.x - qSlashAnimation.getKeyFrame(stateTimer, false).getRegionWidth() / 2, position.y - qSlashAnimation.getKeyFrame(stateTimer, false).getRegionHeight() / 2);
    }

    protected Vector2 getESpinRelativePosition() {
        return new Vector2(position.x - eSwordGrabAnimation.getKeyFrame(stateTimer, false).getRegionWidth() / 2, position.y - eSwordGrabAnimation.getKeyFrame(stateTimer, false).getRegionHeight() / 2);
    }

    public TextureRegion getSprite() {
        return currentRegion;
    }

//    public Vector2 getTargetRelativePosition(){
//        if(qSlashAnimation.getKeyFrame(stateTimer, false) == currentRegion){
//            return getQSlashRelativePosition();
//        } else if(wBurstAnimation.getKeyFrame(stateTimer, false) == currentRegion){
//            return getWBurstRelativePosition();
//        }
//    }

    public TextureRegion getIdleTextureRegion() {return idleTextureRegion;}

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

    public float getHeading(){
        if(!velocity.equals(Vector2.Zero))
            return velocity.angleDeg();
        //if player not moving use the last velocity to keep the player oriented in the last direction
        return previousMovingVelocity.angleDeg();
    }

    protected void movePlayer(float deltaTime){

        //reset velocity vector
        velocity = new Vector2(0, 0);
        //movement on y axis
        if(getState() != State.W) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (getIdleRelativePosition().y < TaskWarrior.HEIGHT - idleTextureRegion.getRegionHeight()) {
                    previousY = position.y;
                    velocity.y = deltaTime * speed;
                    if (getState() == State.Q) {
                        velocity.y -= (deltaTime * speed) / 2;
                    }
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (getIdleRelativePosition().y > 0) {
                    previousY = position.y;
                    velocity.y = -deltaTime * speed;
                    if (getState() == State.Q) {
                        velocity.y += (deltaTime * speed) / 2;
                    }
                }
            }
            //movement on x axis
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (getIdleRelativePosition().x > 0) {
                    previousX = position.x;
                    velocity.x = -deltaTime * speed;
                    if (getState() == State.Q) {
                        velocity.x += (deltaTime * speed) / 2;
                    }
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (getIdleRelativePosition().x < TaskWarrior.WIDTH - idleTextureRegion.getRegionWidth()) {
                    previousX = position.x;
                    velocity.x = deltaTime * speed;
                    if (getState() == State.Q) {
                        velocity.x -= (deltaTime * speed) / 2;
                    }
                }
            }
        }
        //if player is not moving record the last moving velocity
        if(!velocity.equals(Vector2.Zero)){
            previousMovingVelocity = velocity;
        }
        position.add(velocity);
    }


    protected TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case E_GRAB:
                region = eSwordGrabAnimation.getKeyFrame(stateTimer, false);
                relativePosition = getESpinRelativePosition();
                break;
            case E_SPIN:
                region = eSpinAnimation.getKeyFrame(stateTimer, true);
                relativePosition = getESpinRelativePosition();
                break;
            case WALKING:
                region = walkingAnimation.getKeyFrame(stateTimer, true);
                relativePosition = getIdleRelativePosition();
                break;
            case W:
                region = wBurstAnimation.getKeyFrame(stateTimer, false);
                relativePosition = getWBurstRelativePosition();
                break;
            case W_WALKING:
                region = wWalkingAnimation.getKeyFrame(stateTimer, true);
                relativePosition = getIdleRelativePosition();
                break;
            case W_IDLE:
                region = idleInvincibilityTextureRegion;
                relativePosition = getIdleRelativePosition();
                break;
            case Q:
                region = qSlashAnimation.getKeyFrame(stateTimer, false);
                relativePosition = getQSlashRelativePosition();
                break;
            case STANDING:
            default:
                region = idleTextureRegion;
                relativePosition = getIdleRelativePosition();
                break;
        }
        if (previousState == currentState || isPartOfWAnimation()){
            stateTimer += deltaTime;
        } else
            stateTimer = 0;
        previousState = currentState;
        return region;
    }

    private State getState(){
        if(previousState == State.W && isQAnimationFinished && isWAnimationFinished && isEAnimationFinished || stateTimer < 3 && (previousState == State.W_WALKING || previousState == State.W_IDLE) && isWAnimationFinished && isEAnimationFinished){
            relativePosition = getIdleRelativePosition();
            isWAnimationFinished = true;
            isEAnimationFinished = true;
            isQAnimationFinished = true;
            if((Gdx.input.isKeyPressed(Input.Keys.UP)) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                    || (Gdx.input.isKeyPressed(Input.Keys.LEFT)) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
                return State.W_WALKING;
            }
            return State.W_IDLE;
        }
        if(previousState == State.E_GRAB && isQAnimationFinished && isWAnimationFinished && isEAnimationFinished || stateTimer < 3 && previousState == State.E_SPIN && isQAnimationFinished && isWAnimationFinished){
            relativePosition = getESpinRelativePosition();
            isWAnimationFinished = true;
            isEAnimationFinished = true;
            isQAnimationFinished = true;
            return State.E_SPIN;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.W)) && isQAnimationFinished && isEAnimationFinished || !isWAnimationFinished ) {
            relativePosition = getWBurstRelativePosition();
            isWAnimationFinished = wBurstAnimation.isAnimationFinished(stateTimer);
            isQAnimationFinished = true;
            isEAnimationFinished = true;
            return State.W;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.Q)) && isWAnimationFinished && isEAnimationFinished || !isQAnimationFinished) {
            relativePosition = getQSlashRelativePosition();
            isQAnimationFinished = qSlashAnimation.isAnimationFinished(stateTimer);
            isWAnimationFinished = true;
            isEAnimationFinished = true;
            return State.Q;
        }
        if((Gdx.input.isKeyPressed(Input.Keys.E)) && isQAnimationFinished && isWAnimationFinished || !isEAnimationFinished ) {
            relativePosition = getESpinRelativePosition();
            isEAnimationFinished = eSwordGrabAnimation.isAnimationFinished(stateTimer);
            isWAnimationFinished = true;
            isQAnimationFinished = true;
            return State.E_GRAB;
        }
        if(((Gdx.input.isKeyPressed(Input.Keys.UP)) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                || (Gdx.input.isKeyPressed(Input.Keys.LEFT)) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT))) && isQAnimationFinished  && isWAnimationFinished && isEAnimationFinished){
            relativePosition = getIdleRelativePosition();
            isQAnimationFinished = true;
            isWAnimationFinished = true;
            isEAnimationFinished = true;
            return State.WALKING;
        }
        relativePosition = getIdleRelativePosition();
        isQAnimationFinished = true;
        isWAnimationFinished = true;
        isEAnimationFinished = true;
        return State.STANDING;
    }
    private boolean isPartOfWAnimation(){
        return (previousState==State.W_WALKING && currentState==State.W_IDLE) || (previousState==State.W_IDLE && currentState==State.W_WALKING);
    }

    //setters
    public void setCurrentRegion(TextureRegion currentRegion) {
        this.currentRegion = currentRegion;
    }
    public void setPlayerRectangle(Rectangle playerRectangle) {
        this.playerRectangle = playerRectangle;
    }
    public void setPositionX(float position) {
        this.position.x = position;
    }
    public void setPositionY(float position) {
        this.position.y = position;
    }


}
