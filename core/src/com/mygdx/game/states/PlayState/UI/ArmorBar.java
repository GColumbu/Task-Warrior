package com.mygdx.game.states.PlayState.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ArmorBar {
    // armor bar dimension
    private final int HEALTH_BAR_WIDTH = 165;
    private final int HEALTH_BAR_HEIGHT = 22;

    // armor bar offset from UI
    private final int OFFSET_X = 19;
    private final int OFFSET_Y = 70;

    // progress bar utils
    private final ProgressBar.ProgressBarStyle progressBarStyle;
    private ProgressBar progressBar;

    // progress bar variable
    float playerMaxArmor;

    public ArmorBar(float playerMaxArmor) {

        // ProgressBarStyle creates the style of the healthbar
        progressBarStyle = new ProgressBar.ProgressBarStyle();
        this.playerMaxArmor = playerMaxArmor;
    }

    protected void draw(SpriteBatch spriteBatch, float posX, float posY, float playerArmor, float cameraZoom){
        updateProgressBar(cameraZoom);
        progressBar.setScale(cameraZoom);
        progressBar.setValue(playerArmor);
        progressBar.setAnimateDuration(0.02f);
        progressBar.setPosition(posX + (OFFSET_X * cameraZoom), posY + (OFFSET_Y * cameraZoom));
        progressBar.setWidth(HEALTH_BAR_WIDTH * cameraZoom);
        progressBar.setHeight(HEALTH_BAR_HEIGHT * cameraZoom);
        progressBar.draw(spriteBatch, 1f);
    }

    protected void updateProgressBar(float cameraZoom){
        // creates the appearence of the background
        Pixmap pixmap = new Pixmap(HEALTH_BAR_WIDTH, (int)(HEALTH_BAR_HEIGHT * cameraZoom), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.background = drawable;

        // creates the knob
        pixmap = new Pixmap(0, (int)(HEALTH_BAR_HEIGHT * cameraZoom), Pixmap.Format.RGBA8888);
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knob = drawable;

        // creates the knob before
        pixmap = new Pixmap(HEALTH_BAR_WIDTH, (int)(HEALTH_BAR_HEIGHT * cameraZoom), Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(100/255f, 240/255f, 195/255f, 1));
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobBefore = drawable;

        // creates the progress bar
        progressBar = new ProgressBar(0, playerMaxArmor, 0.1f, false, progressBarStyle);
    }
}
