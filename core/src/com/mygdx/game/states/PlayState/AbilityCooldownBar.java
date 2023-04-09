package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AbilityCooldownBar {
    // progress bar dimension
    private final int PROGRESS_BAR_HEIGHT = 3;

    // health bar offset from UI
    private int offsetX;
    private int offsetY;

    ProgressBar.ProgressBarStyle progressBarStyle;

    ProgressBar progressBar;

    private int progressBarWidth;
    private float cooldown;

    protected AbilityCooldownBar(float cooldown, int offsetX, int offsetY , int progressBarWidth){
        this.progressBarWidth = progressBarWidth;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.cooldown = cooldown;
        // ProgressBarStyle creates the style of the healthbar
        progressBarStyle = new ProgressBar.ProgressBarStyle();

        // creates the appearence of the background
        Pixmap pixmap = new Pixmap(progressBarWidth, PROGRESS_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.background = drawable;

        // creates the knob
        pixmap = new Pixmap(2, PROGRESS_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knob = drawable;

        // creates the knob before
        pixmap = new Pixmap(progressBarWidth, PROGRESS_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobBefore = drawable;

        // creates the knob after
        pixmap = new Pixmap(progressBarWidth, PROGRESS_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobAfter = drawable;

        // creates the progress bar
        progressBar = new ProgressBar(0, cooldown, 0.1f, false, progressBarStyle);
    }

    protected void draw(SpriteBatch spriteBatch, float posX, float posY, float cooldownStateTimer){
        updateProgressBar(cooldownStateTimer);
        progressBar.setValue(cooldownStateTimer);
        progressBar.setPosition(posX + offsetX, posY + offsetY);
        progressBar.setWidth(progressBarWidth);
        progressBar.setHeight(PROGRESS_BAR_HEIGHT);
        progressBar.draw(spriteBatch, 1f);
    }

    protected void updateProgressBar(float cooldownStateTimer){
        //updates the knob before
        Pixmap pixmap = new Pixmap(progressBarWidth, PROGRESS_BAR_HEIGHT, Pixmap.Format.RGBA8888);
        changePixmapColor(pixmap, cooldownStateTimer);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobBefore = drawable;

        //updates the progress bar itself
        progressBar = new ProgressBar(0, cooldown, 0.1f, false, progressBarStyle);
    }

    private void changePixmapColor(Pixmap pixmap, float cooldownStateTimer){
        if(cooldownStateTimer >= progressBar.getMaxValue())
            pixmap.setColor(Color.GREEN);
        else
            pixmap.setColor(Color.RED);
    }


}
