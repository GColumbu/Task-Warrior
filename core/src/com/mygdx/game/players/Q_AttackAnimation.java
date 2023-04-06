package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Q_AttackAnimation implements AttackAnimation{

    protected Animation<TextureRegion> qAnimation;
    protected Texture qTexture;

    protected int firstSlashFrames;
    protected int secondSlashFrames;
    protected int thirdSlashFrames;
    protected float frameDuration;

    //TODO: calculate it based on player enhancements
    protected int qAttackDamage;

    protected Q_AttackAnimation(String qTexture1, String qTexture2, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        firstSlashFrames = 12;
        secondSlashFrames = 5;
        thirdSlashFrames = 5;
        this.frameDuration = frameDuration;
        qTexture = new Texture(qTexture1);
        int qSlashFrameWidth = qTexture.getWidth() / 12;
        for(int i=0; i<12; i++){
            frames.add(new TextureRegion(qTexture, i*qSlashFrameWidth, 0, qSlashFrameWidth, qTexture.getHeight()));
        }
        qTexture = new Texture(qTexture2);
        qSlashFrameWidth = qTexture.getWidth() / 10;
        for(int i=0; i<10; i++){
            frames.add(new TextureRegion(qTexture, i*qSlashFrameWidth, 0, qSlashFrameWidth, qTexture.getHeight()));
        }
        qAnimation = new Animation(frameDuration, frames);
    }

    protected float getKeyFrameWidth(float stateTimer){
        return qAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }
    protected float getKeyFrameHeight(float stateTimer){
        return qAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    protected TextureRegion getKeyFrame(float stateTimer){
        return qAnimation.getKeyFrame(stateTimer, false);
    }

    public boolean isFirstSlash(float stateTimer){
        return stateTimer <= firstSlashFrames * frameDuration;
    }

    public boolean isSecondSlash(float stateTimer){
        return stateTimer > firstSlashFrames * frameDuration && stateTimer <= (firstSlashFrames + secondSlashFrames) * frameDuration;
    }
    public boolean isThirdSlash(float stateTimer){
        return stateTimer > firstSlashFrames * frameDuration && stateTimer <= (firstSlashFrames + secondSlashFrames + thirdSlashFrames) * frameDuration;
    }

    @Override
    public boolean isAnimationFinished(float stateTimer) {
        return qAnimation.isAnimationFinished(stateTimer);
    }
}
