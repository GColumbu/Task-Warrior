package com.mygdx.game.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
                ;
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
    public Rectangle getQAttackRange(){
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

    //angled attack range (returns Polygon)
    public Polygon getAngledQAttackDamage() {
        //duration and limit of first slash
        if (stateTimer <= 0.15f * 12) {
            qAnimation.qAttackDamage = 1;
            return getAngledQFirstPartAttackRange();
        }
        //duration and limit of the second (rotative) slash
        else if (stateTimer > 0.15f * 12 && stateTimer < 0.15f * 18){
            qAnimation.qAttackDamage = 2;
            return getAngledQSecondPartAttackRange();
        }
        //duration and limit of third slash
        qAnimation.qAttackDamage = 3;
        return getAngledQThirdPartAttackRange();
    }


    // q first part
    private Rectangle getQFirstPartAttackRange(){
        if(getHeading() == 0) {
            return new Rectangle(getIdleRelativePosition().x + 35,
                    getIdleRelativePosition().y - 125, 200, 310);
        } else if (getHeading() == 90) {
            return new Rectangle(getIdleRelativePosition().x - 100,
                    getIdleRelativePosition().y + 35, 305, 175);
        } else if (getHeading() == 180) {
            return new Rectangle(getIdleRelativePosition().x - 125,
                    getIdleRelativePosition().y - 125, 190, 310);
        } else if (getHeading() == 270) {
            return new Rectangle(getIdleRelativePosition().x - 100,
                    getIdleRelativePosition().y - 150, 305, 195);
        }
        return null;
    }
        // angled
    private Polygon getAngledQFirstPartAttackRange(){
        float x = getIdleRelativePosition().x;
        float y = getIdleRelativePosition().y;
        float width = getIdleTextureRegion().getRegionWidth();
        float height = getIdleTextureRegion().getRegionHeight();
        if(getHeading() == 45) {
            float[] vertices = {x + width + 50, y + height - 150, x + width + 180, y + height - 20, x + 70, y + height + 200, x - 65, y + height + 60};
            return new Polygon(vertices);
        } else if(getHeading() == 135) {
            float[] vertices = {x - 55, y - 75, x - 180, y + 50, x + 40, y + height + 200, x + 160, y + 140};
            return new Polygon(vertices);
        } else if(getHeading() == 225) {
            float[] vertices = {x - 55, y + height + 70, x - 180, y + 10, x + 40, y - 2* height - 50, x + width + 50, y - 55};
            return new Polygon(vertices);
        } else if (getHeading() == 315) {
            float[] vertices = {x - 55, y - 75, x + width - 30, y - 2 * height - 50, x + 2 * width + 70, y + 20, x + 160, y + 140};
            return new Polygon(vertices);
        }
        return null;
    }

    // q second part
    private Rectangle getQSecondPartAttackRange(){
        if(getHeading() == 0) {
            return new Rectangle(getIdleRelativePosition().x - 110,
                    getIdleRelativePosition().y - 140, 347, 330);
        } else if (getHeading() == 90) {
            return new Rectangle(getIdleRelativePosition().x - 105,
                    getIdleRelativePosition().y - 130, 342, 350);
        } else if (getHeading() == 180) {
            return new Rectangle(getIdleRelativePosition().x - 135,
                    getIdleRelativePosition().y - 130, 350, 345);
        } else if (getHeading() == 270) {
            return new Rectangle(getIdleRelativePosition().x - 130,
                    getIdleRelativePosition().y - 150, 342, 350);
        }
        return null;
    }
        // angled
    private Polygon getAngledQSecondPartAttackRange(){
        float x = getIdleRelativePosition().x;
        float y = getIdleRelativePosition().y;
        float width = getIdleTextureRegion().getRegionWidth();
        float height = getIdleTextureRegion().getRegionHeight();
        if(getHeading() == 45) {
            float[] vertices = {x + 110, y - 3 * height - 10, x + width + 215, y + height + 15, x + 20, y + height + 220, x - width - 80, y - 10};
            return new Polygon(vertices);
        }
        if(getHeading() == 135) {
            float[] vertices = {x + 90, y - 3 * height +10, x + width + 185, y + height + 25, x, y + height + 225, x - width - 100, y + 10};
            return new Polygon(vertices);
        }
        if(getHeading() == 225) {
            float[] vertices = {x + 75, y - 3 * height - 15, x + width + 185, y + height, x, y + height + 215, x - width - 100, y};
            return new Polygon(vertices);
        }
        if(getHeading() == 315) {
            float[] vertices = {x + 100, y - 3 * height - 20, x + width + 195, y + height - 5, x + 10, y + height + 190, x - width - 85, y - 30};
            return new Polygon(vertices);
        }
        return null;
    }

    // q third part
    private Rectangle getQThirdPartAttackRange(){
        if(getHeading() == 0) {
            return new Rectangle(getQSlashRelativePosition().x + qAnimation.getKeyFrameWidth(stateTimer) / 2 - 13,
                    getQSlashRelativePosition().y, qAnimation.getKeyFrameWidth(stateTimer) / 2, qAnimation.getKeyFrameHeight(stateTimer));
        } else if (getHeading() == 90) {
            return new Rectangle(getQSlashRelativePosition().x + 50, getQSlashRelativePosition().y + qAnimation.getKeyFrameHeight(stateTimer) / 2 - 20,
                    qAnimation.getKeyFrameWidth(stateTimer) - 100, qAnimation.getKeyFrameHeight(stateTimer) / 2 + 50);
        } else if (getHeading() == 180) {
            return new Rectangle(getQSlashRelativePosition().x,
                    getQSlashRelativePosition().y, qAnimation.getKeyFrameWidth(stateTimer) / 2, qAnimation.getKeyFrameHeight(stateTimer));
        } else if (getHeading() == 270) {
            return new Rectangle(getQSlashRelativePosition().x + 50, getQSlashRelativePosition().y - 75,
                    qAnimation.getKeyFrameWidth(stateTimer) - 100, qAnimation.getKeyFrameHeight(stateTimer) / 2 + 125);
        }
        return null;
    }
        // angled
    private Polygon getAngledQThirdPartAttackRange(){
        float x = getIdleRelativePosition().x;
        float y = getIdleRelativePosition().y;
        float width = getIdleTextureRegion().getRegionWidth();
        float height = getIdleTextureRegion().getRegionHeight();
        if(getHeading() == 45) {
            float[] vertices = {x + width + 50,
                    y + height - 150,
                    x + width + 180,
                    y + height - 20,
                    x + 70,
                    y + height + 200,
                    x - 65,
                    y + height + 60
            };
            return new Polygon(vertices);
        } else if(getHeading() == 135) {
            float[] vertices = {x - 55,
                    y - 75,
                    x - 180,
                    y + 50,
                    x + 40,
                    y + height + 200,
                    x + 160,
                    y + 140};
            return new Polygon(vertices);
        } else if(getHeading() == 225) {
            float[] vertices = {x - 55,
                    y + height + 70,
                    x - 180,
                    y + 10,
                    x + 40,
                    y - 2* height - 50,
                    x + width + 50,
                    y - 55};
            return new Polygon(vertices);
        } else if (getHeading() == 315) {
            float[] vertices = {x - 55,
                    y - 75,
                    x + width - 30,
                    y - 2 * height - 50,
                    x + 2 * width + 70,
                    y + 20,
                    x + 160,
                    y + 140};
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

    public Rectangle getWAttackRange(){
        return new Rectangle(getWBurstRelativePosition().x, getWBurstRelativePosition().y, wAnimation.getBurstKeyFrameWidth(stateTimer), wAnimation.getBurstKeyFrameHeight(stateTimer));
    }

    // E spell
    protected Vector2 getESpinRelativePosition() {
        return new Vector2(position.x - eAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - eAnimation.getKeyFrameHeight(stateTimer) / 2);
    }

    public int getEAttackDamage() {
        return eAnimation.eSpinAttackDamage;
    }

    public Rectangle getEAttackRange(){
        return new Rectangle(getESpinRelativePosition().x, getESpinRelativePosition().y, eAnimation.getSpinKeyFrame(stateTimer).getRegionWidth(), eAnimation.getSpinKeyFrame(stateTimer).getRegionHeight());
    }

    // movement function
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

    public boolean isAngled() {
        return getHeading() == 45 || getHeading() == 135 || getHeading() == 225 || getHeading() == 315;
    }

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
