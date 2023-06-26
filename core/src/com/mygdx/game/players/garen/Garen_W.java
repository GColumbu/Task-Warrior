package com.mygdx.game.players.garen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.players.AttackAnimation;

import java.util.ArrayList;

public class Garen_W extends AttackAnimation {
    private final float COOLDOWN = 10;
    private final int INVINCIBILITY_DURATION = 3;
        //BURST
    protected Animation<TextureRegion> wBurstAnimation;
    protected Texture wBurstTexture;
    protected int wBurstFrames;
    protected float wBurstFrameDuration;
    protected float burstDuration;
    protected float wAttackDamage = 10;
        //WALKING
    protected Animation<TextureRegion> wWalkingAnimation;
    protected Texture wWalkingTexture;
    protected int wWalkingFrames;
    protected float wWalkingFrameDuration;
        //IDLE
    protected TextureRegion idleInvincibilityTextureRegion;
    protected Circle invincibilityRange;

    protected Garen_W(String wBurstFilePath, String wWalkingFilePath, String wIdleTexture,  String wIconFilePath, float wBurstFrameDuration, float wWalkingFrameDuration, String wSoundFilePath) {
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
        this.soundEffects = new ArrayList<>();
        this.soundEffects.add(Gdx.audio.newSound(Gdx.files.internal(wSoundFilePath)));
    }
    // GETTERS
    protected TextureRegion getBurstKeyFrame(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false);
    }
    protected TextureRegion getWalkingKeyFrame(float stateTimer){
        return wWalkingAnimation.getKeyFrame(stateTimer, true);
    }

    public void setInvincibilityRange(Circle invincibilityRange) {
        this.invincibilityRange = invincibilityRange;
    }

    // TIMING METHODS

    public boolean isBurst(float stateTimer, boolean forDamage){
        if(forDamage)
            return stateTimer > 6 * wBurstFrameDuration && stateTimer <= wBurstFrames * wBurstFrameDuration;
        return stateTimer <= wBurstFrames * wBurstFrameDuration;
    }

    // OVERRIDE METHODS
    @Override
    public float getKeyFrameWidth(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    @Override
    public float getKeyFrameHeight(float stateTimer){
        return wBurstAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    public int getKeyFrameIndex(float stateTimer){
       return wBurstAnimation.getKeyFrameIndex(stateTimer);
    }

    @Override
    public boolean isAnimationFinished(float stateTimer) {
        return stateTimer > burstDuration + INVINCIBILITY_DURATION;
    }
}
