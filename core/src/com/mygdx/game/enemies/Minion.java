package com.mygdx.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.players.PlayerChampion;

public class Minion extends Enemy {
    private final static int MINION_MAX_SPEED = 175;
    private final static int MINION_MAX_FORCE = 50;

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
    public Minion(int x, int y) {
        super(x, y, MINION_MAX_SPEED, MINION_MAX_FORCE);
        stateTimer = 0;
        currentState = State.WALKING;
        idleTextureRegion = new TextureRegion(new Texture("minion_idle.png"));
        currentRegion = idleTextureRegion;
        walkingAnimation = new WalkingAnimation("minion_walk.png", 0.1f);
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        //applySteeringBehaviour(flee(player.getPosition().cpy(), deltaTime));
        //applySteeringBehaviour(pursue(player, deltaTime));
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(position.x, position.y, getSprite().getRegionWidth(), getSprite().getRegionHeight()));
        moveAndRecognizeCollision(player, deltaTime);
    }
    @Override
    public TextureRegion getSprite(){
        return currentRegion;
    }

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

    public void moveAndRecognizeCollision(PlayerChampion player, float deltaTime){
        // apply steering behaviour if colision not detected
        if(!enemyRectangle.overlaps(player.getPlayerRectangle())){
            isInRange = false;
            applySteeringBehaviour(pursue(player, deltaTime));
        }else{
            isInRange = true;
            noOverlapping(player);
        }
    }

}
