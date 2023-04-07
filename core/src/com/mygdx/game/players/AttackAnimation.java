package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;

public abstract class AttackAnimation {
    protected Texture attackIcon;
    public abstract boolean isAnimationFinished(float stateTimer);
    public abstract float getKeyFrameWidth(float stateTimer);
    public abstract float getKeyFrameHeight(float stateTimer);
    public Texture getAttackIcon(){
        return attackIcon;
    };
}
