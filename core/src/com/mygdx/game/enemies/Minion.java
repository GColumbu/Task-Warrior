package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.players.PlayerChampion;

public class Minion extends Enemy {
    //constants TODO: maybe calculate them dynamically based on difficulty
    private final static int MINION_MAX_SPEED = 150;
    private final static int MINION_MAX_FORCE = 50;
    private final static int MINION_HEALTH = 300;
    private final static float MINION_DAMAGE = 0.2F;

    public Minion(int x, int y) {
        super(x, y, MINION_MAX_SPEED, MINION_MAX_FORCE, MINION_HEALTH, MINION_DAMAGE);
        stateTimer = 0;
        currentState = State.WALKING;
        idleTextureRegion = new TextureRegion(new Texture("assets/minion/minion_idle.png"));
        currentRegion = idleTextureRegion;
        walkingAnimation = new MinionAnimation("assets/minion/minion_walk.png", 0.1f, 12);
        walkingDamageAnimation = new MinionAnimation("assets/minion/minion_walk_damage.png", 0.1f, 12);
        attackAnimation = new MinionAnimation("assets/minion/minion_attack.png", 0.12f, 7);
        attackDamageAnimation = new MinionAnimation("assets/minion/minion_attack_damage.png", 0.12f, 7);
        dyingAnimation = new MinionAnimation("assets/minion/minion_death.png", 0.07f, 4);
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        //applySteeringBehaviour(flee(player.getPosition().cpy(), deltaTime));
        //applySteeringBehaviour(pursue(player, deltaTime));
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(relativePosition.x + 17, relativePosition.y, getSprite().getRegionWidth() - 34 , getSprite().getRegionHeight()));
        setMinionSenseRange(new Circle(position.x, position.y, 100));
        calculateDamage(player);
    }


}
