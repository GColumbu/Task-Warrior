package com.mygdx.game.states.PlayState.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HealthBar {
    // health bar dimension
    private final int HEALTH_BAR_WIDTH = 165;
    private final int HEALTH_BAR_HEIGHT = 22;

    // health bar offset from UI
    private final int OFFSET_X = 19;
    private final int OFFSET_Y = 47;

    // color change variables
    private final float sixtyPercentPlayerHealth;
    private final float thirtyPercentPlayerHealth;

    // progress bar utils
    private final ProgressBar.ProgressBarStyle progressBarStyle;
    private ProgressBar progressBar;

    // progress bar variable
    float playerMaxHealth;
    protected HealthBar(float playerMaxHealth){
        // ProgressBarStyle creates the style of the healthbar
        progressBarStyle = new ProgressBar.ProgressBarStyle();
        this.playerMaxHealth = playerMaxHealth;
        this.sixtyPercentPlayerHealth = (float)(60.0/100.0) * playerMaxHealth;
        this.thirtyPercentPlayerHealth = (float)(30.0/100.0) * playerMaxHealth;
    }

    protected void draw(SpriteBatch spriteBatch, float posX, float posY, float playerHealth, float cameraZoom){
        updateProgressBar(playerHealth, cameraZoom);
        progressBar.setScale(cameraZoom);
        progressBar.setValue(playerHealth);
        progressBar.setAnimateDuration(0.02f);
        progressBar.setPosition(posX + (OFFSET_X * cameraZoom), posY + (OFFSET_Y * cameraZoom));
        progressBar.setWidth(HEALTH_BAR_WIDTH * cameraZoom);
        progressBar.setHeight(HEALTH_BAR_HEIGHT * cameraZoom);
        progressBar.draw(spriteBatch, 1f);
    }

    protected void updateProgressBar(float playerHealth, float cameraZoom){
        // creates the appearence of the background
        Pixmap pixmap = new Pixmap((int)(HEALTH_BAR_WIDTH * cameraZoom), (int)(HEALTH_BAR_HEIGHT * cameraZoom), Pixmap.Format.RGBA8888);
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
        pixmap = new Pixmap((int)(HEALTH_BAR_WIDTH * cameraZoom), (int)(HEALTH_BAR_HEIGHT * cameraZoom), Pixmap.Format.RGBA8888);
        changePixmapColor(pixmap, playerHealth);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobBefore = drawable;

        // creates the progress bar
        progressBar = new ProgressBar(0, playerMaxHealth, 0.1f, false, progressBarStyle);

    }
    private void changePixmapColor(Pixmap pixmap, float playerHealth){
        if(playerHealth >= sixtyPercentPlayerHealth)
            pixmap.setColor(Color.GREEN);
        else if(playerHealth >= thirtyPercentPlayerHealth)
            pixmap.setColor(Color.ORANGE);
        else
            pixmap.setColor(Color.RED);
    }
}
