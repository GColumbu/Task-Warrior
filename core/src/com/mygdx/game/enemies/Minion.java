package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.players.PlayerChampion;

public class Minion extends Enemy {
    private final static int MINION_MAX_SPEED = 150;
    private final static int MINION_MAX_FORCE = 50;
    public Minion(int x, int y) {
        super(x, y, MINION_MAX_SPEED, MINION_MAX_FORCE);
        sprite = new TextureRegion(new Texture("triangle.png"));
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        //applySteeringBehaviour(flee(player.getPosition().cpy(), deltaTime));
        //applySteeringBehaviour(pursue(player, deltaTime));

        setEnemyRectangle(new Rectangle(position.x, position.y, getSprite().getRegionWidth(), getSprite().getRegionHeight()));
        moveAndRecognizeCollision(player, deltaTime);
    }

    public void moveAndRecognizeCollision(PlayerChampion player, float deltaTime){
        // apply steering behaviour if colision not detected
        if(!enemyRectangle.overlaps(player.getPlayerRectangle())){
            applySteeringBehaviour(pursue(player, deltaTime));
        }else{
            noOverlapping(player);
        }
    }
}
