package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Q_AttackAnimation {

    protected Animation<TextureRegion> qAnimation;
    protected Texture qTexture;

    public Q_AttackAnimation(String qTexture1, String qTexture2, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
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
    }

    public float getKeyFrameWidth(float stateTimer){
        return qAnimation.getKeyFrame(stateTimer, false).getRegionWidth();
    }
    public float getKeyFrameHeight(float stateTimer){
        return qAnimation.getKeyFrame(stateTimer, false).getRegionHeight();
    }

    public TextureRegion getKeyFrame(float stateTimer){
        return qAnimation.getKeyFrame(stateTimer, false);
    }

    public boolean isAnimationFinished(float stateTimer){
        return qAnimation.isAnimationFinished(stateTimer);
    }
}
