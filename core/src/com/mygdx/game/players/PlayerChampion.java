package com.mygdx.game.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    public enum State {STANDING, WALKING, Q, E, W}
    public State currentState;
    public State previousState;
    protected float stateTimer;

    //animations and textures
    protected TextureRegion currentRegion;
    protected TextureRegion idleTextureRegion;
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
            case Q:
                relativePosition = getQSlashRelativePosition();
                break;
            case W:
                if (wAnimation.isBurst(stateTimer, false))
                    relativePosition = getWBurstRelativePosition();
                else
                    relativePosition = getIdleRelativePosition();
                break;
            case E:
                relativePosition = getESpinRelativePosition();
                break;
            case WALKING:
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
    public Q_AttackAnimation getQ_Animation(){
        return qAnimation;
    }
    protected Vector2 getQSlashRelativePosition() {
        return new Vector2(position.x - qAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - qAnimation.getKeyFrameHeight(stateTimer) / 2);
    }

    public int getQAttackDamage() {
        return qAnimation.qAttackDamage;
    }

    // normal attack range (returns Rectangle)
    public Shape2D getQAttackRange(){
        //duration and limit of first slash
        if(qAnimation.isFirstSlash(stateTimer)){
            qAnimation.qAttackDamage = 1;
            return getQFirstPartAttackRange();
        }
        //duration and limit of the second (rotative) slash
        else if (qAnimation.isSecondSlash(stateTimer)){
            qAnimation.qAttackDamage = 2;
            return getQSecondPartAttackRange();
        }
        //duration and limit of third slash
        else if (qAnimation.isThirdSlash(stateTimer)) {
            qAnimation.qAttackDamage = 3;
            return getQThirdPartAttackRange();
        }
        return null;
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
    public W_AttackAnimation getW_Animation(){
        return wAnimation;
    }
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

    public E_AttackAnimation getE_Animation(){
        return eAnimation;
    }
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
        State currentState = getState();
        if(currentState != State.W || currentState == State.W && !wAnimation.isBurst(stateTimer, false)) {
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
    // refactor
    protected TextureRegion getFrame(float deltaTime){
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case Q:
                region = qAnimation.getKeyFrame(stateTimer);
                break;
            case W:
                if (wAnimation.isBurst(stateTimer, false))
                    region = wAnimation.getBurstKeyFrame(stateTimer);
                else if (isMoving())
                    region = wAnimation.getWalkingKeyFrame(stateTimer);
                else
                    region = wAnimation.idleInvincibilityTextureRegion;
                break;
            case E:
                if (eAnimation.isSwordGrab(stateTimer))
                    region = eAnimation.getGrabKeyFrame(stateTimer);
                else
                    region = eAnimation.getSpinKeyFrame(stateTimer);
                break;
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

    // update state function
    //TODO: don't let abilities over another
    public State getState(){

        //Q checking state
        if((Gdx.input.isKeyPressed(Input.Keys.Q)) && areAnimationsOngoing(State.Q) || !qAnimation.isAnimationFinished(stateTimer) && previousState == State.Q) {
            return State.Q;
        }

        // W checking state
        if((Gdx.input.isKeyPressed(Input.Keys.W)) && areAnimationsOngoing(State.W) || !wAnimation.isAnimationFinished(stateTimer) && previousState == State.W) {
            return State.W;
        }

        //E checking state
        if((Gdx.input.isKeyPressed(Input.Keys.E)) && areAnimationsOngoing(State.E) || !eAnimation.isAnimationFinished(stateTimer) && previousState == State.E) {
            return State.E;
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

    private boolean areAnimationsOngoing(State state){
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
