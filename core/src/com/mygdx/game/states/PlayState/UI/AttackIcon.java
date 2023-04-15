package com.mygdx.game.states.PlayState.UI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;

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
