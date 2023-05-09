package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.mygdx.game.players.PlayerChampion;

import java.util.List;

public class Runner extends Enemy{
    private final static int RUNNER_MAX_SPEED = 300;
    private final static int RUNNER_MAX_FORCE = 20;
    private final static int RUNNER_HEALTH = 300;
    private final static float RUNNER_DAMAGE = 0F;
    private final static float ATTACK_COOLDOWN = 0F;
    public Runner(int x, int y){
        super(x, y, RUNNER_MAX_SPEED, RUNNER_MAX_FORCE, RUNNER_HEALTH, RUNNER_DAMAGE, ATTACK_COOLDOWN);
        stateTimer = 0;
        idleTextureRegion = new TextureRegion(new Texture("assets/play screen/runner/runner_idle.png"));
        currentState = State.WALKING;
        currentRegion = idleTextureRegion;
        walkingAnimation = new EnemyAnimation("assets/play screen/runner/runner_walk.png", 0.07f, 12);
        walkingDamageAnimation = new EnemyAnimation("assets/play screen/runner/runner_walk_dmg.png", 0.07f, 12);
        dyingAnimation = new EnemyAnimation("assets/play screen/runner/runner_death.png", 0.07f, 4);
    }
    @Override
    public void update(PlayerChampion player, float deltaTime) {
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(getWalkingRelativePosition().x, getWalkingRelativePosition().y, idleTextureRegion.getRegionWidth(), idleTextureRegion.getRegionHeight()));
        setEnemySenseRange(new Circle(position.x, position.y, 200));
        calculateDamage(player, 0);
    }

    @Override
    public void move(PlayerChampion player, List<Enemy> minions, float deltaTime) {
        separation(getNearbyEnemies(minions, false));
        addBehavior(player, deltaTime);
    }

    @Override
    public Shape2D getAttackRange() {
        return null;
    }

    @Override
    protected boolean isAttackTiming() {
        return false;
    }

    @Override
    protected void addBehavior(PlayerChampion player, float deltaTime) {
        // apply flee steering behaviour if collision not detected
        if (!enemyRectangle.overlaps(player.getPlayerRectangle())) {
            // make runner apply flee steering behavior only if it's inside a predefined circle
            if (isCollision(player.getRunnerBehaviorRange(), enemyRectangle))
                currentSteeringBehavior = flee(player.getPosition().cpy(), deltaTime);
            else {
                currentSteeringBehavior = wander(deltaTime);
            }
        }
        // don't let player overlap minion
        else if (enemyRectangle.overlaps(player.getPlayerRectangle())) {
            isInRange = false;
            noOverlappingWithPlayer(player);
        }
        currentState = State.WALKING;
        applySteeringBehaviour(currentSteeringBehavior, deltaTime);

        if (health <= 0)
            currentState = State.DEAD;
    }
}
