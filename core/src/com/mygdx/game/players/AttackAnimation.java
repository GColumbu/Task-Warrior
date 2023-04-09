package com.mygdx.game.players;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Shape2D;

public abstract class AttackAnimation {
    // UI ability icon
    protected Texture attackIcon;
    // cooldown
    protected float cooldownStateTimer;
    protected float cooldownDuration;

    // abstract functions
    public abstract boolean isAnimationFinished(float stateTimer);
    public abstract float getKeyFrameWidth(float stateTimer);
    public abstract float getKeyFrameHeight(float stateTimer);

    // getters
    public Texture getAttackIcon(){
        return attackIcon;
    }
    public float getCooldownDuration() { return cooldownDuration;}
    public float getCooldownStateTimer() {
        return cooldownStateTimer;
    }
    public void resetCooldown(){
        cooldownStateTimer = 0;
    }
}
