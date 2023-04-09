package com.mygdx.game.players.garen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.players.AttackAnimation;

public class Garen_W extends AttackAnimation {
    private final float COOLDOWN = 10;
    private final int INVINCIBILITY_DURATION = 3;
        //BURST
    protected Animation<TextureRegion> wBurstAnimation;
    protected Texture wBurstTexture;
    protected int wBurstFrames;
    protected float wBurstFrameDuration;
    protected float burstDuration;

    //TODO: calculate it based on player enhancements
    protected float wAttackDamage = 10;
        //WALKING
    protected Animation<TextureRegion> wWalkingAnimation;
    protected Texture wWalkingTexture;
    protected int wWalkingFrames;
    protected float wWalkingFrameDuration;
        //IDLE
    protected TextureRegion idleInvincibilityTextureRegion;

    protected Garen_W(String wBurstFilePath, String wWalkingFilePath, String wIdleTexture,  String wIconFilePath, float wBurstFrameDuration, float wWalkingFrameDuration) {
        cooldownDuration = COOLDOWN;
        cooldownStateTimer = cooldownDuration;
        wBurstFrames = 10;
        wWalkingFrames = 12;
        this.wBurstTexture = new Texture(wBurstFilePath);
        this.wWalkingTexture = new Texture(wWalkingFilePath);
        int wBurstFrameWidth = wBurstTexture.getWidth() / wBurstFrames;
        int wWalkingFrameWidth = wWalkingTexture.getWidth() / wWalkingFrames;
        Array<TextureRegion> frames = new Array<>();
        for(int i=0; i<wBurstFrames; i++){
            frames.add(new TextureRegion(wBurstTexture, i*wBurstFrameWidth, 0, wBurstFrameWidth, wBurstTexture.getHeight()));
        }
        wBurstAnimation = new Animation(wBurstFrameDuration, frames);
        this.wBurstFrameDuration = wBurstFrameDuration;
        frames.clear();
        for(int i=0; i<wWalkingFrames; i++){
            frames.add(new TextureRegion(wWalkingTexture, i*wWalkingFrameWidth, 0, wWalkingFrameWidth, wWalkingTexture.getHeight()));
        }
        wWalkingAnimation = new Animation(wWalkingFrameDuration, frames);
        this.wWalkingFrameDuration = wWalkingFrameDuration;
        idleInvincibilityTextureRegion = new TextureRegion(new Texture(wIdleTexture));
        attackIcon = new Texture(wIconFilePath);
        burstDuration = wBurstAnimation.getAnimationDuration();
    }

    protected TextureRegion getBurstKeyFrame(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false);
    }
    protected TextureRegion getWalkingKeyFrame(float stateTimer){
        return wWalkingAnimation.getKeyFrame(stateTimer, true);
    }

    public boolean isBurst(float stateTimer, boolean forCollision){
        if(forCollision)
            return stateTimer> 6 * wBurstFrameDuration && stateTimer <= wBurstFrames * wBurstFrameDuration;
        return stateTimer <= wBurstFrames * wBurstFrameDuration;
    }

    @Override
    public float getKeyFrameWidth(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    @Override
    public float getKeyFrameHeight(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    @Override
    public boolean isAnimationFinished(float stateTimer) {
        return stateTimer > burstDuration + INVINCIBILITY_DURATION;
    }
}
