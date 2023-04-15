package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.players.PlayerChampion;

public class Minimap {
    // minimap dimensions
    private static final int MINIMAP_WIDTH = 276;
    private static final int MINIMAP_HEIGHT = 276;
    private final Texture minimap;

    private OrthographicCamera minimapCamera;

    private final Viewport minimapViewport;

    public Minimap(Texture minimap) {
        this.minimap = minimap;
        minimapCamera = new OrthographicCamera(MINIMAP_WIDTH, MINIMAP_HEIGHT);
        minimapCamera.setToOrtho(false, MINIMAP_WIDTH, MINIMAP_HEIGHT);
        this.minimapViewport = new ScreenViewport(minimapCamera);
    }

    protected void draw(SpriteBatch spriteBatch, PlayerChampion player, Viewport gameViewport){
        Vector3 playerWorldPosition = transformCoordinates(player.getPosition(), gameViewport);

        //ShapeRenderer shapeRenderer = new ShapeRenderer();
        //shapeRenderer.setProjectionMatrix(minimapViewport.getCamera().combined);
        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.BLACK);
        minimapViewport.apply();
        spriteBatch.setProjectionMatrix(minimapCamera.combined);
        spriteBatch.begin();
        spriteBatch.draw(minimap, 0, 0);
        //shapeRenderer.circle(playerWorldPosition.x, 276-playerWorldPosition.y, 5);
        spriteBatch.end();
        //shapeRenderer.end();
    }

    protected void update(){
        minimapViewport.update(276, 276);
        minimapViewport.setScreenPosition(Gdx.graphics.getWidth() - MINIMAP_WIDTH, 0);
    }

    private Vector3 transformCoordinates(Vector2 position, Viewport gameViewport){
        Vector3 playerWorldPosition = new Vector3(position.x, position.y, 0);
        gameViewport.getCamera().project(playerWorldPosition);
        minimapViewport.getCamera().unproject(playerWorldPosition);
        return playerWorldPosition;
    }
}
