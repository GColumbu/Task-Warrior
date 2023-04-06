package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class E_AttackAnimation implements AttackAnimation{
    private final int SPIN_DURATION = 3;
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

    //TODO: calculate it based on player enhancements
    protected int eSpinAttackDamage = 2;


    protected E_AttackAnimation(String eSpinPath, float eGrabFrameDuration, float eSpinFrameDuration) {
        eGrabFrames = 3;
        eSpinFrames = 6;
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
        this.eSpinFrameDuration = eSpinFrameDuration;
    }

    protected float getKeyFrameWidth(float stateTimer){
        return eSpinAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    protected float getKeyFrameHeight(float stateTimer){
        return eSpinAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
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

    @Override
    public boolean isAnimationFinished(float stateTimer) {
        return stateTimer > swordGrabDuration + SPIN_DURATION;
    }
}
