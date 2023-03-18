package com.mygdx.game.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


public class Player extends PlayerChampion{

    public Player(int x, int y){
        super(x, y, 500);
        idleTextureRegion = new TextureRegion(new Texture("idle.png"));
        idleInvincibilityTextureRegion = new TextureRegion(new Texture("idle_w.png"));
        currentState = State.STANDING;
        stateTimer = 0;
        walkingTexture = new Texture("walk.png");
        wBurstTexture = new Texture("invincibility_burst.png");
        wWalkingTexture = new Texture("invincibility_walk.png");
        qSlashTexture = new Texture("slash_combo_part1.png");
        eSpinTexture = new Texture("spin.png");
        int walkingFrameWidth = walkingTexture.getWidth() / 12;
        int wBurstFrameWidth = wBurstTexture.getWidth() / 10;
        int wWalkingFrameWidth = wWalkingTexture.getWidth() / 12;
        int qSlashFrameWidth = qSlashTexture.getWidth() / 12;
        int eSpinFrameWidth = eSpinTexture.getWidth() / 6;
        Array<TextureRegion> frames = new Array<>();
        for(int i=0; i<12; i++){
            frames.add(new TextureRegion(walkingTexture, i*walkingFrameWidth, 0, walkingFrameWidth, walkingTexture.getHeight()));
        }
        walkingAnimation = new Animation(0.05f, frames);
        frames.clear();
        for(int i=0; i<10; i++){
            frames.add(new TextureRegion(wBurstTexture, i*wBurstFrameWidth, 0, wBurstFrameWidth, wBurstTexture.getHeight()));
        }
        wBurstAnimation = new Animation(0.07f, frames);
        frames.clear();
        for(int i=0; i<12; i++){
            frames.add(new TextureRegion(wWalkingTexture, i*wWalkingFrameWidth, 0, wWalkingFrameWidth, wWalkingTexture.getHeight()));
        }
        wWalkingAnimation = new Animation(0.05f, frames);
        frames.clear();
        for(int i=0; i<12; i++){
            frames.add(new TextureRegion(qSlashTexture, i*qSlashFrameWidth, 0, qSlashFrameWidth, qSlashTexture.getHeight()));
        }
        qSlashTexture = new Texture("slash_combo_part2.png");
        qSlashFrameWidth = qSlashTexture.getWidth() / 10;
        for(int i=0; i<10; i++){
            frames.add(new TextureRegion(qSlashTexture, i*qSlashFrameWidth, 0, qSlashFrameWidth, qSlashTexture.getHeight()));
        }
        qSlashAnimation = new Animation(0.07f, frames);
        frames.clear();
        for(int i=0; i<3; i++){
            frames.add(new TextureRegion(eSpinTexture, i*eSpinFrameWidth, 0, eSpinFrameWidth, eSpinTexture.getHeight()));
        }
        eSwordGrabAnimation = new Animation(0.17f, frames);
        frames.clear();
        for(int i=3; i<6; i++){
            frames.add(new TextureRegion(eSpinTexture, i*eSpinFrameWidth, 0, eSpinFrameWidth, eSpinTexture.getHeight()));
        }
        eSpinAnimation = new Animation(0.05f, frames);
        frames.clear();
        currentRegion = idleTextureRegion;
    }

    @Override
    public void update(float deltaTime) {
        setCurrentRegion(getFrame(deltaTime));
        setPlayerRectangle(new Rectangle(getIdleRelativePosition().x, getIdleRelativePosition().y, getIdleTextureRegion().getRegionWidth(), getIdleTextureRegion().getRegionHeight()));
        movePlayer(deltaTime);
    }
}
