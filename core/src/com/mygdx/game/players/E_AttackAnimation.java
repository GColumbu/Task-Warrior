package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class E_AttackAnimation {
    protected Texture eSpinTexture;
    protected Animation<TextureRegion> eSwordGrabAnimation;
    protected Animation<TextureRegion> eSpinAnimation;

    protected E_AttackAnimation(String eSpinPath, float swordGrabFrameDuration, float spinFrameDuration) {
        this.eSpinTexture = new Texture(eSpinPath);
        int eSpinFrameWidth = eSpinTexture.getWidth() / 9;
        Array<TextureRegion> frames = new Array<>();
        for(int i=0; i<3; i++){
            frames.add(new TextureRegion(eSpinTexture, i*eSpinFrameWidth, 0, eSpinFrameWidth, eSpinTexture.getHeight()));
        }
        eSwordGrabAnimation = new Animation(swordGrabFrameDuration, frames);
        frames.clear();
        for(int i=3; i<8; i++){
            frames.add(new TextureRegion(eSpinTexture, i*eSpinFrameWidth, 0, eSpinFrameWidth, eSpinTexture.getHeight()));
        }
        eSpinAnimation = new Animation(spinFrameDuration, frames);
    }

    protected float getKeyFrameWidth(float stateTimer){
        return eSwordGrabAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    protected float getKeyFrameHeight(float stateTimer){
        return eSwordGrabAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    protected TextureRegion getGrabKeyFrame(float stateTimer){
        return eSwordGrabAnimation.getKeyFrame(stateTimer, false);
    }

    protected TextureRegion getSpinKeyFrame(float stateTimer){
        return eSpinAnimation.getKeyFrame(stateTimer, true);
    }

    protected boolean isAnimationFinished(float stateTimer){
        return eSpinAnimation.isAnimationFinished(stateTimer);
    }

}
