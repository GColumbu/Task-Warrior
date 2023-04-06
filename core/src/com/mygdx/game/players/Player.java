package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


public class Player extends PlayerChampion{

    public Player(int x, int y){
        super(x, y, 500);
        walkingAnimation = new WalkingAnimation("walk.png", 0.05f);
        wAnimation = new W_AttackAnimation("invincibility_burst.png", "invincibility_walk.png", "idle_w.png", 0.07f, 0.05f);
        qAnimation = new Q_AttackAnimation("slash_combo_part1.png", "slash_combo_part2.png", 0.07f);
        eAnimation = new E_AttackAnimation("spin.png", 0.07f, 0.05f);
        idleTextureRegion = new TextureRegion(new Texture("idle.png"));
        currentRegion = idleTextureRegion;
        currentState = State.STANDING;
        stateTimer = 0;
    }

    @Override
    public void update(float deltaTime) {
        setRelativePosition();
        setCurrentRegion(getFrame(deltaTime));
        setPlayerRectangle(new Rectangle(getIdleRelativePosition().x, getIdleRelativePosition().y, getIdleTextureRegion().getRegionWidth(), getIdleTextureRegion().getRegionHeight()));
        movePlayer(deltaTime);
    }
}
