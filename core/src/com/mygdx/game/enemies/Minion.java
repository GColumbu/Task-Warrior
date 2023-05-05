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
    private final static int MINION_MAX_FORCE = 30;
    private final static int MINION_HEALTH = 300;
    private final static float MINION_DAMAGE = 0.5F;
    private final static float ATTACK_COOLDOWN = 0F;

    public Minion(int x, int y) {
        super(x, y, MINION_MAX_SPEED, MINION_MAX_FORCE, MINION_HEALTH, MINION_DAMAGE, ATTACK_COOLDOWN);
        stateTimer = 0;
        currentState = State.WALKING;
        idleTextureRegion = new TextureRegion(new Texture("assets/play screen/minion/minion_idle.png"));
        currentRegion = idleTextureRegion;
        walkingAnimation = new EnemyAnimation("assets/play screen/minion/minion_walk.png", 0.1f, 12);
        walkingDamageAnimation = new EnemyAnimation("assets/play screen/minion/minion_walk_damage.png", 0.1f, 12);
        attackAnimation = new EnemyAnimation("assets/play screen/minion/minion_attack.png", 0.08f, 7);
        attackDamageAnimation = new EnemyAnimation("assets/play screen/minion/minion_attack_damage.png", 0.08f, 7);
        dyingAnimation = new EnemyAnimation("assets/play screen/minion/minion_death.png", 0.07f, 4);
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        //applySteeringBehaviour(flee(player.getPosition().cpy(), deltaTime));
        //applySteeringBehaviour(pursue(player, deltaTime));
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(getWalkingRelativePosition().x + 10, getWalkingRelativePosition().y, idleTextureRegion.getRegionWidth()/2, idleTextureRegion.getRegionHeight()));
        setEnemySenseRange(new Circle(position.x, position.y, 100));
        calculateDamage(player, 5);
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
                maxSpeed = 450;
                currentSteeringBehavior = flee(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior, deltaTime);
                maxSpeed = 150;
                currentState = State.WALKING;
            }
            // apply seek steering behaviour if collision not detected and is not W Invincibility
            else if (!enemyRectangle.overlaps(player.getPlayerRectangle())) {
                currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior, deltaTime);
                currentState = State.WALKING;
            }
        }
        else {
            // apply seek steering behaviour if collision not detected
            if (!enemyRectangle.overlaps(player.getPlayerRectangle())) {
                currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior, deltaTime);
                currentState = State.WALKING;
            }
        }

        // don't let player overlap minion
        if (enemyRectangle.overlaps(player.getPlayerRectangle())) {
            noOverlappingWithPlayer(player);
            currentState = State.ATTACK;
        }

        // minion dies
        if (health <= 0)
            currentState = State.DEAD;
    }

    @Override
    public Rectangle getAttackRange(){
        return new Rectangle(getAttackRelativePosition().x, getAttackRelativePosition().y, getSprite().getRegionWidth(), getSprite().getRegionHeight());
    }

    private void avoidRunner(List<Enemy> runners, float deltaTime){
        for(Enemy runner : runners){
            currentSteeringBehavior = flee(runner.position.cpy(), deltaTime);
            applySteeringBehaviour(currentSteeringBehavior, deltaTime);
        }
    }
}
