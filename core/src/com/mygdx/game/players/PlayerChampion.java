package com.mygdx.game.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.TaskWarrior;

public abstract class PlayerChampion {

    //vectors
    protected Vector2 position;
    protected float previousX;
    protected float previousY;
    protected Vector2 relativePosition;
    protected int speed;
    protected Vector2 velocity;
    protected Vector2 previousMovingVelocity = new Vector2(0, 0);

    //player states
    public enum State {STANDING, WALKING, Q, E_GRAB, E_SPIN, W, W_WALKING, W_IDLE}
    public State currentState;
    public State previousState;
    protected float stateTimer;

    //animations and textures
    protected TextureRegion currentRegion;
    protected TextureRegion idleTextureRegion;
    protected TextureRegion idleInvincibilityTextureRegion;
    protected WalkingAnimation walkingAnimation;
    protected W_AttackAnimation wAnimation;
    protected Q_AttackAnimation qAnimation;
    protected E_AttackAnimation eAnimation;

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

    protected void setRelativePosition() {
        currentState = getState();
        switch (currentState) {
            case E_GRAB:
            case E_SPIN:
                relativePosition = getESpinRelativePosition();
                break;
            case W:
                relativePosition = getWBurstRelativePosition();
                break;
            case Q:
                relativePosition = getQSlashRelativePosition();
                break;
            case WALKING:
            case W_WALKING:
            case W_IDLE:
            case STANDING:
                relativePosition = getIdleRelativePosition();
                break;
        }
    }

    //getters
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

    // Q spell methods
    protected Vector2 getQSlashRelativePosition() {
        return new Vector2(position.x - qAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - qAnimation.getKeyFrameHeight(stateTimer) / 2);
    }

    public int getQAttackDamage() {
        return qAnimation.qAttackDamage;
    }

    // normal attack range (returns Rectangle)
    public Shape2D getQAttackRange(){
        //duration and limit of first slash
        if(stateTimer <= 0.15f * 12){
            qAnimation.qAttackDamage = 1;
            return getQFirstPartAttackRange();
        }
        //duration and limit of the second (rotative) slash
        else if (stateTimer > 0.15f * 12 && stateTimer < 0.15f * 18){
            qAnimation.qAttackDamage = 2;
            return getQSecondPartAttackRange();
        }
        //duration and limit of third slash
        qAnimation.qAttackDamage = 3;
        return getQThirdPartAttackRange();
    }

    // q first part
    private Shape2D getQFirstPartAttackRange(){
        float x = getIdleRelativePosition().x;
        float y = getIdleRelativePosition().y;
        if(getHeading() == 0) {
            return new Rectangle(x + 35, y - 125, 200, 310);
        } else if(getHeading() == 45) {
            float[] vertices = {x + 156, y - 82, x + 286, y + 48, x + 70, y + 268, x - 65, y + 128};
            return new Polygon(vertices);
        } else if (getHeading() == 90) {
            return new Rectangle(x - 100, y + 35, 305, 175);
        } else if(getHeading() == 135) {
            float[] vertices = {x - 55, y - 75, x - 180, y + 50, x + 40, y + 268, x + 160, y + 140};
            return new Polygon(vertices);
        } else if (getHeading() == 180) {
            return new Rectangle(x - 125, y - 125, 190, 310);
        } else if (getHeading() == 225) {
            float[] vertices = {x - 55, y + 138, x - 180, y + 10, x + 40, y - 186, x + 156, y - 55};
            return new Polygon(vertices);
        } else if (getHeading() == 270) {
            return new Rectangle( x - 100,y - 150, 305, 195);
        } else if (getHeading() == 315) {
            float[] vertices = {x - 55, y - 75, x + 76, y - 186, x + 282, y + 20, x + 160, y + 140};
            return new Polygon(vertices);
        }
        return null;
    }

    // q second part
    private Shape2D getQSecondPartAttackRange(){
        if(getHeading() == 0) {
            return new Circle(position.x + 15, position.y - 5, 175);
        } else if(getHeading() == 45) {
            return new Circle(position.x + 13, position.y + 6, 175);
        } else if (getHeading() == 90) {
            return new Circle(position.x + 9, position.y + 15, 175);
        } else if(getHeading() == 135) {
            return new Circle(position.x - 5, position.y + 15, 175);
        } else if (getHeading() == 180) {
            return new Circle(position.x - 13, position.y + 7, 175);
        } else if(getHeading() == 225) {
            return new Circle(position.x - 13, position.y - 5, 175);
        } else if (getHeading() == 270) {
            return new Circle(position.x - 7, position.y - 12, 175);
        } else if(getHeading() == 315) {
            return new Circle(position.x + 3, position.y - 12, 175);
        }
        return null;
    }

    // q third part
    private Shape2D getQThirdPartAttackRange(){
        float x = getIdleRelativePosition().x;
        float y = getIdleRelativePosition().y;
        if(getHeading() == 0) {
            float[] vertices = {x + 35, y - 485, x + 250, y - 450, x + 420, y - 370, x + 595, y - 150, x + 625, y + 50, x + 605, y + 200, x + 515,
                    y + 375, x + 450, y + 440, x + 390, y + 470, x + 320, y + 520, x + 170, y + 560, x + 50, y + 550};
            return new Polygon(vertices);
        }  if(getHeading() == 45) {
            float[] vertices = {x + 410, y - 345, x + 515, y - 220, x + 572, y - 100, x + 610, y + 50, x + 605, y + 200, x + 515, y + 375, x + 460,
                    y + 440, x + 390, y + 500, x + 320, y + 550, x + 170, y + 590, x, y + 590, x - 140, y + 540, x - 325, y + 385};
            return new Polygon(vertices);
        } else if (getHeading() == 90) {
            float[] vertices = {x + 575, y + 15, x + 560, y + 200, x + 520, y + 300, x + 480, y + 375, x + 450, y + 440, x + 390, y + 500, x + 320,
                    y + 540, x + 170, y + 590, x, y + 605, x - 140, y + 565, x - 270, y + 500, x - 430, y + 300, x - 465, y + 150, x - 457, y + 15};
            return new Polygon(vertices);
        } else if(getHeading() == 135) {
            float[] vertices = {x + 430, y + 385, x + 280, y + 510, x + 100, y + 575, x, y + 587, x - 140, y + 565, x - 270, y + 500, x - 430, y + 330,
                    x - 490, y + 150, x - 490, y - 75, x - 360, y - 270, x - 300, y - 320};
            return new Polygon(vertices);
        } else if (getHeading() == 180) {
            float[] vertices = {x + 70, y + 550, x -50, y + 540, x - 140, y + 520, x - 270, y + 475, x - 430, y + 330, x - 490, y + 150, x - 490, y - 75,
                    x - 425, y - 270, x - 250, y - 420, x - 100, y - 470, x + 70, y - 470};
            return new Polygon(vertices);
        } else if(getHeading() == 225) {
            float[] vertices = {x - 300, y + 415, x - 430, y + 230, x - 470, y + 150, x - 490, y - 75, x - 425, y - 270, x - 250, y - 430, x - 100, y - 495,
                    x + 70, y - 510, x + 230, y - 480, x + 330, y - 400, x + 430, y - 310};
            return new Polygon(vertices);
        } else if (getHeading() == 270) {
            float[] vertices = {x - 470, y + 50, x - 445, y - 150, x - 405, y - 270, x - 250, y - 430, x - 100, y - 510, x + 70, y - 530, x + 230, y - 505,
                    x + 330, y - 440, x + 470, y - 310, x + 550, y - 150, x + 570, y + 50};
            return new Polygon(vertices);
        } else if (getHeading() == 315) {
            float[] vertices = {x - 320, y - 320, x - 225, y - 400, x - 100, y - 495, x + 70, y - 520, x + 230, y - 505,
                    x + 345, y - 440, x + 495, y - 310, x + 587, y - 150, x + 597, y + 50, x + 555, y + 205, x + 505, y + 315, x + 400, y + 400};
            return new Polygon(vertices);
        }
        return null;
    }

    // W spell
    protected Vector2 getWBurstRelativePosition() {
        return new Vector2(position.x - wAnimation.getBurstKeyFrameWidth(stateTimer) / 2, position.y - wAnimation.getBurstKeyFrameHeight(stateTimer) / 2);
    }

    public int getWAttackDamage() {
        return wAnimation.wAttackDamage;
    }

    public Circle getWAttackRange(){
        return new Circle(position.x, position.y, 270);
    }

    // E spell
    protected Vector2 getESpinRelativePosition() {
        return new Vector2(position.x - eAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - eAnimation.getKeyFrameHeight(stateTimer) / 2);
    }

    public int getEAttackDamage() {
        return eAnimation.eSpinAttackDamage;
    }

    public Circle getEAttackRange(){
        return new Circle(position.x, position.y, 310);
    }

    // movement function
    protected void movePlayer(float deltaTime){
        // reset velocity vector
        velocity = new Vector2(0, 0);
        // movement on y axis
        if(getState() != State.W) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (getIdleRelativePosition().y < TaskWarrior.HEIGHT - idleTextureRegion.getRegionHeight()) {
                    velocity.y = deltaTime * speed;
                    if (getState() == State.Q) {
                        velocity.y -= (deltaTime * speed) / 2;
                    }
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (getIdleRelativePosition().y > 0) {
                    velocity.y = -deltaTime * speed;
                    if (getState() == State.Q) {
                        velocity.y += (deltaTime * speed) / 2;
                    }
                }
            }
            // movement on x axis
             if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (getIdleRelativePosition().x > 0) {
                    velocity.x = -deltaTime * speed;
                    if (getState() == State.Q) {
                        velocity.x += (deltaTime * speed) / 2;
                    }
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (getIdleRelativePosition().x < TaskWarrior.WIDTH - idleTextureRegion.getRegionWidth()) {
                    velocity.x = deltaTime * speed;
                    if (getState() == State.Q) {
                        velocity.x -= (deltaTime * speed) / 2;
                    }
                }
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
    protected TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case E_GRAB:
                region = eAnimation.getGrabKeyFrame(stateTimer);
                break;
            case E_SPIN:
                region = eAnimation.getSpinKeyFrame(stateTimer);
                break;
            case WALKING:
                region = walkingAnimation.getKeyFrame(stateTimer);
                break;
            case W:
                region = wAnimation.getBurstKeyFrame(stateTimer);
                break;
            case W_WALKING:
                region = wAnimation.getWalkingKeyFrame(stateTimer);
                break;
            case W_IDLE:
                region = idleInvincibilityTextureRegion;
                break;
            case Q:
                region = qAnimation.getKeyFrame(stateTimer);
                break;
            default:
            case STANDING:
                region = idleTextureRegion;
                break;
        }
        if (previousState == currentState || isPartOfWAnimation()){
            stateTimer += deltaTime;
        } else
            stateTimer = 0;
        previousState = currentState;
        return region;
    }

    // update state function
    public State getState(){
        // W checking state
            //BURST
        if((Gdx.input.isKeyPressed(Input.Keys.W)) && areAnimationsFinished(qAnimation, eAnimation) && wAnimation.isWalkingFinished || !wAnimation.isBurstFinished) {
            wAnimation.updateBurstAnimationFinished(stateTimer);
            return State.W;
        }
            //WALKING-IDLE
        if(previousState == State.W && wAnimation.isBurstFinished
                || stateTimer < 3 && (previousState == State.W_WALKING || previousState == State.W_IDLE)){
            wAnimation.isWalkingFinished = false;
            if(isMoving()) {
                return State.W_WALKING;
            }
            return State.W_IDLE;
        }
        if(stateTimer > 3 && (previousState == State.W_WALKING || previousState == State.W_IDLE))
            wAnimation.isWalkingFinished = true;

        //E checking state
            //SWORD GRAB
        if((Gdx.input.isKeyPressed(Input.Keys.E)) && areAnimationsFinished(qAnimation, wAnimation) && eAnimation.isESpinAnimationFinished || !eAnimation.isEGrabAnimationFinished) {
            eAnimation.updateGrabAnimationFinished(stateTimer);
            return State.E_GRAB;
        }
            //SPIN
        if(previousState == State.E_GRAB && eAnimation.isEGrabAnimationFinished
                || stateTimer < 3 && previousState == State.E_SPIN){
            eAnimation.isESpinAnimationFinished = false;
            return State.E_SPIN;
        }
        if(stateTimer > 3 && previousState == State.E_SPIN)
            eAnimation.isESpinAnimationFinished = true;

        //Q checking state
        if((Gdx.input.isKeyPressed(Input.Keys.Q)) && areAnimationsFinished(wAnimation, eAnimation) || !qAnimation.isQAnimationFinished) {
            qAnimation.updateQAnimationFinished(stateTimer);
            return State.Q;
        }

        //WALKING checking state
        if(isMoving()){
            return State.WALKING;
        }
        //IDLE checking state
        return State.STANDING;
    }

    //update state functions

    private boolean isMoving(){
        return (Gdx.input.isKeyPressed(Input.Keys.UP)) || (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                || (Gdx.input.isKeyPressed(Input.Keys.LEFT)) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT));
    }

    private boolean isPartOfWAnimation(){
        return (previousState==State.W_WALKING && currentState==State.W_IDLE) || (previousState==State.W_IDLE && currentState==State.W_WALKING);
    }

    private boolean areAnimationsFinished(AttackAnimation ... animations){
        for(AttackAnimation animation : animations){
            if(!animation.isAnimationFinished()){
                return false;
            }
        }
        return true;
    }
}
