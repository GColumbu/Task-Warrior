package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.players.PlayerChampion;

import java.util.List;

public class Runner extends Enemy{
    private final static int RUNNER_MAX_SPEED = 300;
    private final static int RUNNER_MAX_FORCE = 100;
    private final static int RUNNER_HEALTH = 300;
    private final static float RUNNER_DAMAGE = 0F;
    public Runner(int x, int y){
        super(x, y, RUNNER_MAX_SPEED, RUNNER_MAX_FORCE, RUNNER_HEALTH, RUNNER_DAMAGE);
        stateTimer = 0;
        currentState = State.WALKING;
        idleTextureRegion = new TextureRegion(new Texture("assets/play screen/minion/minion_idle.png")); //TODO: change with runner idle
        currentRegion = idleTextureRegion;
        walkingAnimation = new EnemyAnimation("assets/play screen/runner/runner_walk.png", 0.07f, 12);
        walkingDamageAnimation = new EnemyAnimation("assets/play screen/runner/runner_walk_dmg.png", 0.07f, 12);
        dyingAnimation = new EnemyAnimation("assets/play screen/runner/runner_death.png", 0.07f, 4);
    }
    @Override
    public void update(PlayerChampion player, float deltaTime) {
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(relativePosition.x + 17, relativePosition.y, getSprite().getRegionWidth() - 34, getSprite().getRegionHeight()));
        setMinionSenseRange(new Circle(position.x, position.y, 200));
        calculateDamage(player, 0);
    }

    @Override
    public void move(PlayerChampion player, List<Enemy> minions, float deltaTime) {
        separation(getNearbyEnemies(minions, false));
        addBehavior(player, deltaTime);

        // if runner is not moving record the last moving velocity
        if(!velocity.equals(Vector2.Zero)){
            previousMovingVelocity = velocity;
        }
    }

    @Override
    protected void addBehavior(PlayerChampion player, float deltaTime) {
        // apply flee steering behaviour if collision not detected
        if (!enemyRectangle.overlaps(player.getPlayerRectangle())) {
            // make runner apply flee steering behavior only if it's inside a predefined circle
            if(isCollision(player.getRunnerBehaviorRange(), enemyRectangle))
                currentSteeringBehavior = flee(player.getPosition().cpy(), deltaTime);
            else {
                velocity = new Vector2(0, 0);
                currentSteeringBehavior = new Vector2(0, 0);
            }
            isInRange = false;
        }
        // don't let player overlap minion
        else if (enemyRectangle.overlaps(player.getPlayerRectangle())){
            isInRange = false;
            noOverlappingWithPlayer(player);
        }
        applySteeringBehaviour(currentSteeringBehavior);
    }
}
