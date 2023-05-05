package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.players.PlayerChampion;

import java.util.List;

public class Troll extends Enemy{
    private final static int TROLL_MAX_SPEED = 120;
    private final static int TROLL_MAX_FORCE = 2;
    private final static int TROLL_HEALTH = 400;
    private final static float TROLL_DAMAGE = 3F;
    private final static float ATTACK_COOLDOWN = 5F;

    private Circle trollChargeRange;
    private Vector2 attackTarget;

    public Troll(int x, int y) {
        super(x, y, TROLL_MAX_SPEED, TROLL_MAX_FORCE, TROLL_HEALTH, TROLL_DAMAGE, ATTACK_COOLDOWN);
        currentState = State.WALKING;
        idleTextureRegion = new TextureRegion(new Texture("assets/play screen/minion/minion_idle.png"));
        currentRegion = idleTextureRegion;
        walkingAnimation = new EnemyAnimation("assets/play screen/troll/troll_walk.png", 0.2f, 11);
        walkingDamageAnimation = new EnemyAnimation("assets/play screen/troll/troll_walk_damage.png", 0.2f, 11);
        attackAnimation = new EnemyAnimation("assets/play screen/troll/troll_attack.png", 0.2f, 10);
        //attackDamageAnimation = new EnemyAnimation("assets/play screen/troll/troll_attack_damage.png", 0.08f, 7);
        //dyingAnimation = new EnemyAnimation("assets/play screen/troll/troll_death.png", 0.07f, 4);
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(getWalkingRelativePosition().x , getWalkingRelativePosition().y, 150, 150)); //TODO: replace with idleTextureRegion width and height
        setEnemySenseRange(new Circle(position.x, position.y, 100));
        trollChargeRange = new Circle(position.x, position.y, 575);
        calculateDamage(player, 7);
    }

    @Override
    public void move(PlayerChampion player, List<Enemy> minions, float deltaTime) {
        separation(getNearbyEnemies(minions, false));
        addBehavior(player, deltaTime);
    }

    @Override
    public Shape2D getAttackRange() {
        Vector2 velocityProjection = this.velocity.cpy();
        velocityProjection.setLength(100);
        return new Circle(velocityProjection.x, velocityProjection.y, 300);
    }

    @Override
    protected void addBehavior(PlayerChampion player, float deltaTime) {
        System.out.println(cooldownStateTimer);
        if(isCooldownFinished() && (isCollidingWithAttackRange(trollChargeRange, player.getPlayerRectangle()) || isAttackOngoing())){
            // get position of the player when the troll "sees" him
//            if(currentState != State.ATTACK){
//                attackTarget = player.getPosition().cpy();
//                //velocity = new Vector2(0, 0);
//            }
            // accelerate to match the dash animation and seek/pursue the position of the player when it was first "seen"
            if(attackAnimation.getKeyFrameIndex(stateTimer) >= 5 && attackAnimation.getKeyFrameIndex(stateTimer) <= 7){
                maxSpeed = 2000;
                maxForce = 200;
                currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior, deltaTime);
                maxForce = 2;
                maxSpeed = 120;
            }
            currentState = State.ATTACK;
        } else if (!enemyRectangle.overlaps(player.getPlayerRectangle()) && !isCollidingWithAttackRange(trollChargeRange, player.getPlayerRectangle())) {
            currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
            applySteeringBehaviour(currentSteeringBehavior, deltaTime);
            currentState = State.WALKING;
        }

        // don't let player overlap minion
        if (enemyRectangle.overlaps(player.getPlayerRectangle())) {
            noOverlappingWithPlayer(player);
        }

        // troll dies
        if (health <= 0)
            currentState = State.DEAD;
    }

    private boolean isAttackOngoing(){
        return currentState == State.ATTACK && !attackAnimation.animation.isAnimationFinished(stateTimer);
    }

    private boolean isCooldownFinished(){
        return cooldownStateTimer >= cooldownDuration;
    }

    // get range for the attack charge (for debug purposes)
    public Circle getTrollChargeRange() {
        return trollChargeRange;
    }

}
