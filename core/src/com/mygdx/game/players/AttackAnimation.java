package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;

public abstract class AttackAnimation {
    protected Texture attackIcon;

    protected float cooldownStateTimer;

    protected float cooldownDuration;
    public abstract boolean isAnimationFinished(float stateTimer);
    public abstract float getKeyFrameWidth(float stateTimer);
    public abstract float getKeyFrameHeight(float stateTimer);
    public Texture getAttackIcon(){
        return attackIcon;
    }

    public float getCooldownDuration() {
        return cooldownDuration;
    }

    public float getCooldownStateTimer() {
        return cooldownStateTimer;
    }
    public void resetCooldown(){
        cooldownStateTimer = 0;
    }
}
