package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class W_AttackAnimation implements AttackAnimation{
    //BURST
    protected Animation<TextureRegion> wBurstAnimation;
    protected Texture wBurstTexture;
    protected boolean isBurstFinished = true;
    //WALKING
    protected Animation<TextureRegion> wWalkingAnimation;
    protected Texture wWalkingTexture;
    protected boolean isWalkingFinished = true;

    public W_AttackAnimation(String wBurstFilePath, String wWalkingFilePath, float wBurstFrameDuration, float wWalkingFrameDuration) {
        this.wBurstTexture = new Texture(wBurstFilePath);
        this.wWalkingTexture = new Texture(wWalkingFilePath);
        int wBurstFrameWidth = wBurstTexture.getWidth() / 10;
        int wWalkingFrameWidth = wWalkingTexture.getWidth() / 12;
        Array<TextureRegion> frames = new Array<>();
        for(int i=0; i<10; i++){
            frames.add(new TextureRegion(wBurstTexture, i*wBurstFrameWidth, 0, wBurstFrameWidth, wBurstTexture.getHeight()));
        }
        wBurstAnimation = new Animation(wBurstFrameDuration, frames);
        frames.clear();
        for(int i=0; i<12; i++){
            frames.add(new TextureRegion(wWalkingTexture, i*wWalkingFrameWidth, 0, wWalkingFrameWidth, wWalkingTexture.getHeight()));
        }
        wWalkingAnimation = new Animation(wWalkingFrameDuration, frames);
    }

    public float getBurstKeyFrameWidth(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    public float getBurstKeyFrameHeight(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    public TextureRegion getBurstKeyFrame(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false);
    }
    public TextureRegion getWalkingKeyFrame(float stateTimer){
        return wWalkingAnimation.getKeyFrame(stateTimer, true);
    }

    protected void updateBurstAnimationFinished(float stateTimer){
        isBurstFinished = wBurstAnimation.isAnimationFinished(stateTimer);
    }

    protected void updateWalkingAnimationFinished(float stateTimer){
        isWalkingFinished = wWalkingAnimation.isAnimationFinished(stateTimer);
    }

    @Override
    public boolean isAnimationFinished() {
        return isBurstFinished && isWalkingFinished;
    }
}
