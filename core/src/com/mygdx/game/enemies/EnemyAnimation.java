package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class EnemyAnimation {
    protected Animation<TextureRegion> animation;
    protected Texture texture;

    protected EnemyAnimation(String walkingTexturePath, float frameDuration, int nrOfFrames) {
        this.texture = new Texture(walkingTexturePath);
        Array<TextureRegion> frames = new Array<>();
        int walkingFrameWidth = texture.getWidth() / nrOfFrames;
        for(int i=0; i<nrOfFrames; i++){
            frames.add(new TextureRegion(texture, i*walkingFrameWidth, 0, walkingFrameWidth, texture.getHeight()));
        }
        animation = new Animation(frameDuration, frames);
    }

    protected float getKeyFrameWidth(float stateTimer){
        return animation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    protected float getKeyFrameHeight(float stateTimer){
        return animation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    protected TextureRegion getKeyFrame(float stateTimer, boolean looping){
        return animation.getKeyFrame(stateTimer, looping);
    }
    protected int getKeyFrameIndex(float stateTimer){
        return animation.getKeyFrameIndex(stateTimer);
    }

}
