package com.mygdx.game.players.garen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.players.AttackAnimation;

import java.util.ArrayList;

public class Garen_E extends AttackAnimation {
    private final int SPIN_DURATION = 3;
    private final float COOLDOWN = 7;
    protected Texture eSpinTexture;

        //SWORD GRAB
    protected Animation<TextureRegion> eSwordGrabAnimation;
    protected float swordGrabDuration;
    protected int eGrabFrames;
    protected float eGrabFrameDuration;
        //SPIN
    protected Animation<TextureRegion> eSpinAnimation;
    protected int eSpinFrames;
    protected float eSpinFrameDuration;
    protected float eSpinAttackDamage = 2;


    protected Garen_E(String eSpinPath, String eIconFilePath,  float eGrabFrameDuration, float eSpinFrameDuration, String eSoundFilePath) {
        eGrabFrames = 3;
        eSpinFrames = 6;
        cooldownDuration = COOLDOWN;
        cooldownStateTimer = cooldownDuration;
        this.eSpinTexture = new Texture(eSpinPath);
        int eSpinFrameWidth = eSpinTexture.getWidth() / 9;
        Array<TextureRegion> frames = new Array<>();
        for(int i=0; i<3; i++){
            frames.add(new TextureRegion(eSpinTexture, i*eSpinFrameWidth, 0, eSpinFrameWidth, eSpinTexture.getHeight()));
        }
        eSwordGrabAnimation = new Animation(eGrabFrameDuration, frames);
        this.eGrabFrameDuration = eGrabFrameDuration;
        frames.clear();
        for(int i=3; i<8; i++){
            frames.add(new TextureRegion(eSpinTexture, i*eSpinFrameWidth, 0, eSpinFrameWidth, eSpinTexture.getHeight()));
        }
        eSpinAnimation = new Animation(eSpinFrameDuration, frames);
        eSpinAnimation.setPlayMode(Animation.PlayMode.LOOP);
        this.eSpinFrameDuration = eSpinFrameDuration;
        attackIcon = new Texture(eIconFilePath);
        this.soundEffects = new ArrayList<>();
        this.soundEffects.add(Gdx.audio.newSound(Gdx.files.internal(eSoundFilePath)));
    }

    protected TextureRegion getGrabKeyFrame(float stateTimer){
        return eSwordGrabAnimation.getKeyFrame(stateTimer, false);
    }

    protected TextureRegion getSpinKeyFrame(float stateTimer){
        return eSpinAnimation.getKeyFrame(stateTimer, true);
    }

    public boolean isSwordGrab(float stateTimer){
        return stateTimer <= eGrabFrames * eGrabFrameDuration;
    }

    public int getGrabKeyFrameIndex(float stateTimer){
        return eSwordGrabAnimation.getKeyFrameIndex(stateTimer);
    }

    public int getSpinKeyFrameIndex(float stateTimer){
        return eSpinAnimation.getKeyFrameIndex(stateTimer);
    }

    @Override
    public float getKeyFrameWidth(float stateTimer){
        return eSpinAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    @Override
    public float getKeyFrameHeight(float stateTimer){
        return eSpinAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    @Override
    public boolean isAnimationFinished(float stateTimer) {
        return stateTimer > swordGrabDuration + SPIN_DURATION;
    }

    public void disposeSoundEffects(){
        for (Sound sound : soundEffects){
            sound.dispose();
        }
    }
}
