package com.mygdx.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.mygdx.game.players.PlayerChampion;
import com.mygdx.game.states.PlayState.EnemyTextures;

import java.util.List;

public class Runner extends Enemy{
    private final static int RUNNER_MAX_SPEED = 300;
    private final static int RUNNER_MAX_FORCE = 20;
    private final static int RUNNER_HEALTH = 300;
    private final static float RUNNER_DAMAGE = 0F;
    private final static float ATTACK_COOLDOWN = 0F;
    public Runner(int x, int y, EnemyTextures enemyTextures){
        super(x, y, RUNNER_MAX_SPEED, RUNNER_MAX_FORCE, RUNNER_HEALTH, RUNNER_DAMAGE, ATTACK_COOLDOWN);
        currentRegion = enemyTextures.idleTextureRegion;
        currentState = State.WALKING;
        idleTextureRegion = enemyTextures.idleTextureRegion;
        walkingAnimation = enemyTextures.walkingAnimation;
        walkingDamageAnimation = enemyTextures.walkingDamageAnimation;
        dyingAnimation = enemyTextures.dyingAnimation;
        dyingSoundEffect = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/runner/runner_death.mp3"));
    }
    @Override
    public void update(PlayerChampion player, float deltaTime) {
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(getWalkingRelativePosition().x, getWalkingRelativePosition().y, idleTextureRegion.getRegionWidth(), idleTextureRegion.getRegionHeight()));
        setEnemySenseRange(new Circle(position.x, position.y, 200));
        calculateDamage(player, 0);
        addSoundEffects();
    }

    @Override
    public void move(PlayerChampion player, List<Enemy> minions, float deltaTime) {
        separation(getNearbyEnemies(minions));
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

    private void addSoundEffects(){
        if (currentState == State.DEAD && dyingAnimation.getKeyFrameIndex(stateTimer) == 0){
            dyingSoundEffect.setVolume(dyingSoundEffect.play(), 0.03f);
        }
    }
}
