package com.mygdx.game.states.PlayState;

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
    private final float sixtyPercentPlayerHealth;
    private final float thirtyPercentPlayerHealth;

    ProgressBar.ProgressBarStyle progressBarStyle;

    ProgressBar progressBar;

    float playerMaxHealth;
    protected HealthBar(float playerMaxHealth){
        // ProgressBarStyle creates the style of the healthbar
        progressBarStyle = new ProgressBar.ProgressBarStyle();

        // creates the appearence of the background
        Pixmap pixmap = new Pixmap(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.background = drawable;

        // creates the knob
        pixmap = new Pixmap(0, HEALTH_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knob = drawable;

        // creates the knob before
        pixmap = new Pixmap(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobBefore = drawable;

        // creates the progress bar
        progressBar = new ProgressBar(0, playerMaxHealth, 1, false, progressBarStyle);

        this.playerMaxHealth = playerMaxHealth;
        this.sixtyPercentPlayerHealth = (float)(60.0/100.0) * playerMaxHealth;
        this.thirtyPercentPlayerHealth = (float)(30.0/100.0) * playerMaxHealth;
    }
    protected void draw(SpriteBatch spriteBatch, float posX, float posY, float playerHealth){
        updateProgressBar(playerHealth);
        progressBar.setValue(playerHealth);
        progressBar.setAnimateDuration(0.05f);
        progressBar.setPosition(posX + OFFSET_X, posY + OFFSET_Y);
        progressBar.setWidth(HEALTH_BAR_WIDTH);
        progressBar.setHeight(HEALTH_BAR_HEIGHT);
        progressBar.draw(spriteBatch, 1f);
    }

    protected void updateProgressBar(float playerHealth){
        //updates the knob before
        Pixmap pixmap = new Pixmap(HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        changePixmapColor(pixmap, playerHealth);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobBefore = drawable;

        //updates the progress bar itself
        progressBar = new ProgressBar(0, playerMaxHealth, 10, false, progressBarStyle);
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
