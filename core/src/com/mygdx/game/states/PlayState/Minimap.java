package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.enemies.Enemy;
import com.mygdx.game.enemies.Minion;
import com.mygdx.game.players.PlayerChampion;

import java.util.List;

public class Minimap {
    // minimap dimensions
    private static final int MINIMAP_WIDTH = 276;
    private static final int MINIMAP_HEIGHT = 276;
    private final Texture minimap;
    // green square - player
    TextureRegion player;
    // red square - enemy;
    TextureRegion enemy;

    private OrthographicCamera minimapCamera;

    private final Viewport minimapViewport;

    public Minimap(Texture minimap) {
        this.minimap = minimap;
        minimapCamera = new OrthographicCamera(MINIMAP_WIDTH, MINIMAP_HEIGHT);
        minimapCamera.setToOrtho(false, MINIMAP_WIDTH, MINIMAP_HEIGHT);
        this.minimapViewport = new ScreenViewport(minimapCamera);

        Texture texture = new Texture(Gdx.files.internal("green_circle.png"));
        player = new TextureRegion(texture, texture.getWidth()/2, texture.getHeight()/2, 10, 10);

         texture = new Texture(Gdx.files.internal("red_circle.png"));
        enemy = new TextureRegion(texture, texture.getWidth()/2, texture.getHeight()/2, 10, 10);
    }

    protected void draw(SpriteBatch spriteBatch, Viewport gameViewport, PlayerChampion player, List<Enemy> minions){
        minimapViewport.apply();
        spriteBatch.setProjectionMatrix(minimapCamera.combined);
        spriteBatch.begin();
        spriteBatch.draw(minimap, 0, 0);

        // calculate position and draw player
        Vector3 playerWorldPosition = transformCoordinates(player.getPosition(), gameViewport);
        drawCharacter(this.player, spriteBatch, playerWorldPosition.x, 276-playerWorldPosition.y);

        // calculate position and draw minions
        for(int i=0; i<minions.size(); i++){
            Vector3 enemyWorldPosition = transformCoordinates(minions.get(i).getPosition(), gameViewport);
            drawCharacter(this.enemy, spriteBatch, enemyWorldPosition.x, 276-enemyWorldPosition.y);
        }
        spriteBatch.end();
    }

    protected void update(){
        minimapViewport.update(276, 276); //TODO: update the minimap
        minimapViewport.setScreenPosition(Gdx.graphics.getWidth() - MINIMAP_WIDTH, 0);
    }

    private Vector3 transformCoordinates(Vector2 position, Viewport gameViewport){
        Vector3 playerWorldPosition = new Vector3(position.x, position.y, 0);
        gameViewport.getCamera().project(playerWorldPosition);
        minimapViewport.getCamera().unproject(playerWorldPosition);
        return playerWorldPosition;
    }

    private void drawCharacter(TextureRegion square, SpriteBatch spriteBatch, float x, float y){
        spriteBatch.draw(square, x, y);
    }
}
