package com.mygdx.game.states.PlayState.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AbilityCooldownBar {
    // progress bar dimension
    private final int PROGRESS_BAR_HEIGHT = 37;

    // health bar offset from UI
    private final int offsetX;
    private final int offsetY;

    // camera zoom
    private float cameraZoom;

    private ProgressBar.ProgressBarStyle progressBarStyle;
    private ProgressBar progressBar;
    private final int progressBarWidth;
    private final float cooldown;
    private Color magicColor;

    protected AbilityCooldownBar(float cooldown, int offsetX, int offsetY , int progressBarWidth){
        this.progressBarWidth = progressBarWidth;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.cooldown = cooldown;
        this.magicColor = new Color(100/255f, 240/255f, 195/255f, 1);

        // ProgressBarStyle creates the style of the ability cooldown bar
        progressBarStyle = new ProgressBar.ProgressBarStyle();

        // ProgressBarStyle variables that remain the same
            // creates the knob
        Pixmap pixmap = new Pixmap(0, (int)(PROGRESS_BAR_HEIGHT * cameraZoom), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knob = drawable;
    }

    protected void draw(SpriteBatch spriteBatch, float posX, float posY, float cooldownStateTimer, float cameraZoom){
        this.cameraZoom = cameraZoom;
        updateProgressBar(cooldownStateTimer);
        progressBar.setValue(cooldownStateTimer);
        progressBar.setPosition(posX + (offsetX * cameraZoom), posY + (offsetY * cameraZoom));
        progressBar.setWidth(progressBarWidth * cameraZoom);
        progressBar.setHeight(PROGRESS_BAR_HEIGHT * cameraZoom);
        progressBar.draw(spriteBatch, 1f);
    }

    protected void updateProgressBar(float cooldownStateTimer){
        // creates the appearence of the background
        Pixmap pixmap = new Pixmap((int)(progressBarWidth * cameraZoom), (int)(PROGRESS_BAR_HEIGHT * cameraZoom), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.background = drawable;

        // creates the knob before
        pixmap = new Pixmap((int)(progressBarWidth * cameraZoom), (int)(PROGRESS_BAR_HEIGHT * cameraZoom), Pixmap.Format.RGBA8888);
        changePixmapColor(pixmap, cooldownStateTimer);
        pixmap.fill();
        drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        progressBarStyle.knobBefore = drawable;

        // creates the progress bar
        progressBar = new ProgressBar(0, cooldown, 0.1f, true, progressBarStyle);
    }

    private void changePixmapColor(Pixmap pixmap, float cooldownStateTimer){
        if(cooldownStateTimer >= cooldown)
            pixmap.setColor(magicColor);
        else
            pixmap.setColor(Color.RED);
    }
}
