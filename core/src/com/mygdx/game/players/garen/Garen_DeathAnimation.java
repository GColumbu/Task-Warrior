package com.mygdx.game.players.garen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Garen_DeathAnimation {
    protected Texture deathTexture;
    protected Animation<TextureRegion> deathAnimation;

    protected Garen_DeathAnimation(String deathPath, float deathFrameDuration){
        this.deathTexture = new Texture(deathPath);
        int deathFrameWidth = deathTexture.getWidth() / 8;
        Array<TextureRegion> frames = new Array<>();
        for(int i=0; i<8; i++){
            frames.add(new TextureRegion(deathTexture, i*deathFrameWidth, 0, deathFrameWidth, deathTexture.getHeight()));
        }
        deathAnimation = new Animation(deathFrameDuration, frames);
    }

    protected TextureRegion getKeyFrame(float stateTimer){
        return deathAnimation.getKeyFrame(stateTimer, false);
    }

    public float getKeyFrameWidth(float stateTimer){
        return deathAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }

    public float getKeyFrameHeight(float stateTimer){
        return deathAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }
}
