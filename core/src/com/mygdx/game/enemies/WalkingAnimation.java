package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class WalkingAnimation {
    protected Animation<TextureRegion> walkingAnimation;
    protected Texture walkingTexture;

    protected WalkingAnimation(String walkingTexturePath, float frameDuration) {
        this.walkingTexture = new Texture(walkingTexturePath);
        Array<TextureRegion> frames = new Array<>();
        int walkingFrameWidth = walkingTexture.getWidth() / 12;
        for(int i=0; i<12; i++){
            frames.add(new TextureRegion(walkingTexture, i*walkingFrameWidth, 0, walkingFrameWidth, walkingTexture.getHeight()));
        }
        walkingAnimation = new Animation(frameDuration, frames);
    }

    protected float getKeyFrameWidth(float stateTimer){
        return walkingAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    protected float getKeyFrameHeight(float stateTimer){
        return walkingAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    protected TextureRegion getKeyFrame(float stateTimer){
        return walkingAnimation.getKeyFrame(stateTimer, true);
    }
}

