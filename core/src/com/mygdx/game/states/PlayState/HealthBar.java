package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.players.PlayerChampion;

import java.awt.*;

public class HealthBar {
    private final float UI_HEALTH_WIDTH = 165;
    protected Rectangle healthBar;
    private final int xOffset;
    private final int yOffset;
    private final int height;
    private final float widthMultiplicationfactor;
    private final float sixtyPercentPlayerHealth;
    private final float thirtyPercentPlayerHealth;
    protected HealthBar(int xOffset, int yOffset, int height, float playerMaxHealth){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.height = height;
        this.widthMultiplicationfactor = UI_HEALTH_WIDTH / playerMaxHealth;
        this.sixtyPercentPlayerHealth = (float)(60.0/100.0) * playerMaxHealth;
        this.thirtyPercentPlayerHealth = (float)(30.0/100.0) * playerMaxHealth;
    }

    protected void update(ShapeRenderer shapeRenderer, float playerHealth){
        if(playerHealth >= sixtyPercentPlayerHealth)
            shapeRenderer.setColor(Color.GREEN);
        else if(playerHealth >= thirtyPercentPlayerHealth)
            shapeRenderer.setColor(Color.ORANGE);
        else
            shapeRenderer.setColor(Color.RED);
    }

    protected void draw(ShapeRenderer shapeRenderer, float playerHealth, Vector2 referencePosition, OrthographicCamera camera){
        if(playerHealth > 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setProjectionMatrix(camera.combined);
            update(shapeRenderer, playerHealth);
            healthBar = new Rectangle((int) referencePosition.x + xOffset, (int) referencePosition.y + yOffset, (int) (widthMultiplicationfactor * playerHealth), height);
            shapeRenderer.rect((float) healthBar.getX(), (float) healthBar.getY(), (float) healthBar.getWidth(), (float) healthBar.getHeight());
        }
    }
}
