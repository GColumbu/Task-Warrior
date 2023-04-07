package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.graphics.Texture;

public class AttackIcon {
    //attack icon
    protected Texture attackIconTexture;
    protected int xOffset;
    protected int yOffset;

    public AttackIcon(Texture attackIconTexture, int xOffset, int yOffset) {
        this.attackIconTexture = attackIconTexture;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
}
