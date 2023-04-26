package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.players.PlayerChampion;
import com.mygdx.game.players.garen.Garen;

import java.util.List;

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
        walkingAnimation = new EnemyAnimation("assets/minion/minion_walk.png", 0.1f, 12);
        walkingDamageAnimation = new EnemyAnimation("assets/minion/minion_walk_damage.png", 0.1f, 12);
        attackAnimation = new EnemyAnimation("assets/minion/minion_attack.png", 0.12f, 7);
        attackDamageAnimation = new EnemyAnimation("assets/minion/minion_attack_damage.png", 0.12f, 7);
        dyingAnimation = new EnemyAnimation("assets/minion/minion_death.png", 0.07f, 4);
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        //applySteeringBehaviour(flee(player.getPosition().cpy(), deltaTime));
        //applySteeringBehaviour(pursue(player, deltaTime));
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(relativePosition.x + 17, relativePosition.y, getSprite().getRegionWidth() - 34, getSprite().getRegionHeight()));
        setMinionSenseRange(new Circle(position.x, position.y, 100));
        calculateDamage(player);
    }

    @Override
    public void move(PlayerChampion player, List<Enemy> minions, float deltaTime) {
        separation(getNearbyEnemies(minions, false));
        avoidRunner(getNearbyEnemies(minions, true), deltaTime);
        addBehavior(player, deltaTime);
    }

    @Override
    protected void addBehavior(PlayerChampion player, float deltaTime) {
        // garen custom behavior
        if (player instanceof Garen) {
            Garen garen = (Garen) player;
            // apply flee steering behaviour if is W Invincibility
            if (garen.getState() == PlayerChampion.State.W && !garen.getWAnimation().isBurst(garen.getStateTimer(), false) && isCollision(garen.getInvincibilityRange(), enemyRectangle)) {
                isInRange = false;
                maxSpeed = 450;
                currentSteeringBehavior = flee(player.getPosition().cpy(), deltaTime);
                maxSpeed = 150;
                applySteeringBehaviour(currentSteeringBehavior);
            }
            // apply seek steering behaviour if collision not detected and is not W Invincibility
            else if (!enemyRectangle.overlaps(player.getPlayerRectangle())) {
                isInRange = false;
                currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior);
            }
        }
        else {
            // apply seek steering behaviour if collision not detected
            if (!enemyRectangle.overlaps(player.getPlayerRectangle())) {
                isInRange = false;
                currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior);
            }
        }

        // don't let player overlap minion
        if (enemyRectangle.overlaps(player.getPlayerRectangle())) {
            isInRange = true;
            noOverlappingWithPlayer(player);
        }
    }

    private void avoidRunner(List<Enemy> runners, float deltaTime){
        for(Enemy runner : runners){
            currentSteeringBehavior = flee(runner.position.cpy(), deltaTime);
            applySteeringBehaviour(currentSteeringBehavior);
        }
    }
}
