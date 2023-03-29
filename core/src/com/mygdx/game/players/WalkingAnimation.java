package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class WalkingAnimation {
    protected Animation<TextureRegion> walkingAnimation;
    protected Texture walkingTexture;

    public WalkingAnimation(String walkingFilePath, float frameDuration) {
        this.walkingTexture = new Texture(walkingFilePath);
        int walkingFrameWidth = walkingTexture.getWidth() / 12;
        Array<TextureRegion> frames = new Array<>();
        for(int i=0; i<12; i++){
            frames.add(new TextureRegion(walkingTexture, i*walkingFrameWidth, 0, walkingFrameWidth, walkingTexture.getHeight()));
        }
        walkingAnimation = new Animation(frameDuration, frames);
    }

    public TextureRegion getKeyFrame(float stateTimer){
        return walkingAnimation.getKeyFrame(stateTimer, true);
    }
}
