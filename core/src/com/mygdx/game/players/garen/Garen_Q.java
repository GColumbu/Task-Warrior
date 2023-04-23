package com.mygdx.game.players.garen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.players.AttackAnimation;

public class Garen_Q extends AttackAnimation {
    private final float COOLDOWN = 7;

    protected Animation<TextureRegion> qAnimation;
    protected Texture qTexture;

    protected int firstSlashFrames;
    protected int secondSlashFrames;
    protected int thirdSlashFrames;
    protected float frameDuration;

    //TODO: calculate it based on player enhancements
    protected float qAttackDamage;

    protected Garen_Q(String qTexture1, String qTexture2, String qIconFilePath, float frameDuration) {
        cooldownDuration = COOLDOWN;
        cooldownStateTimer = cooldownDuration;
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
        attackIcon = new Texture(qIconFilePath);
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

    protected int getKeyFrameIndex(float stateTimer){
        return qAnimation.getKeyFrameIndex(stateTimer);
    }

    @Override
    public float getKeyFrameWidth(float stateTimer){
        return qAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }
    @Override
    public float getKeyFrameHeight(float stateTimer){
        return qAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    @Override
    public boolean isAnimationFinished(float stateTimer) {
        return qAnimation.isAnimationFinished(stateTimer);
    }
}
