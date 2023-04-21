package com.mygdx.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.players.PlayerChampion;

import java.util.List;

public class Minion extends Enemy {
    //constants TODO: maybe calculate them dynamically based on difficulty
    private final static int MINION_MAX_SPEED = 175;
    private final static int MINION_MAX_FORCE = 50;
    private final static int MINION_HEALTH = 300;
    private final static float MINION_DAMAGE = 0.2F;

    public Minion(int x, int y) {
        super(x, y, MINION_MAX_SPEED, MINION_MAX_FORCE, MINION_HEALTH, MINION_DAMAGE);
        stateTimer = 0;
        currentState = State.WALKING;
        idleTextureRegion = new TextureRegion(new Texture("assets/minion/minion_idle.png"));
        currentRegion = idleTextureRegion;
        walkingAnimation = new WalkingAnimation("assets/minion/minion_walk.png", 0.1f);
        walkingDamageAnimation = new WalkingAnimation("assets/minion/minion_walk_damage.png", 0.1f);
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        //applySteeringBehaviour(flee(player.getPosition().cpy(), deltaTime));
        //applySteeringBehaviour(pursue(player, deltaTime));
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(relativePosition.x + 17, relativePosition.y, getSprite().getRegionWidth() - 34 , getSprite().getRegionHeight()));
        calculateDamage(player);
    }


}
