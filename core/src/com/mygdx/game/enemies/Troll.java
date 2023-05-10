package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.players.PlayerChampion;
import com.mygdx.game.states.PlayState.EnemyTextures;

import java.util.List;

public class Troll extends Enemy{
    private final static int TROLL_MAX_SPEED = 120;
    private final static int TROLL_MAX_FORCE = 2;
    private final static int TROLL_HEALTH = 400;
    private final static float TROLL_DAMAGE = 3F;
    private final static float ATTACK_COOLDOWN = 5F;

    // troll custom variables
    private Circle trollChargeRange;
    private Vector2 attackTarget;

    //TODO: fix frame drop when troll spawns
    public Troll(int x, int y, EnemyTextures enemyTextures) {
        super(x, y, TROLL_MAX_SPEED, TROLL_MAX_FORCE, TROLL_HEALTH, TROLL_DAMAGE, ATTACK_COOLDOWN);
        attackTarget = new Vector2(0, 0);
        currentState = State.WALKING;
        currentRegion = enemyTextures.idleTextureRegion;
        idleTextureRegion = enemyTextures.idleTextureRegion;
        walkingAnimation = enemyTextures.walkingAnimation;
        walkingDamageAnimation = enemyTextures.walkingDamageAnimation;
        attackAnimation = enemyTextures.attackAnimation;
        attackDamageAnimation = enemyTextures.attackDamageAnimation;
        dyingAnimation = enemyTextures.dyingAnimation;
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(getWalkingRelativePosition().x , getWalkingRelativePosition().y, idleTextureRegion.getRegionWidth(), idleTextureRegion.getRegionHeight()));
        setEnemySenseRange(new Circle(position.x, position.y, 200));
        if(currentState != State.ATTACK)
            trollChargeRange = new Circle(position.x, position.y, 575);
        calculateDamage(player, 7);
    }

    @Override
    public void move(PlayerChampion player, List<Enemy> minions, float deltaTime) {
        separation(getNearbyEnemies(minions));
        addBehavior(player, deltaTime);
    }

    @Override
    public Shape2D getAttackRange() {
        Vector2 velocityProjection = this.velocity.cpy();
        velocityProjection.setLength(280);
        velocityProjection.add(position);
        return new Circle(velocityProjection.x, velocityProjection.y, 300);
    }

    public Vector2 getAttackTarget() {
        return attackTarget;
    }

    @Override
    protected boolean isAttackTiming(){
        return attackAnimation.getKeyFrameIndex(stateTimer) >= 5 && attackAnimation.getKeyFrameIndex(stateTimer) <= 7;
    }

    @Override
    protected void addBehavior(PlayerChampion player, float deltaTime) {
        if(isCooldownFinished() && isCollidingWithAttackRange(trollChargeRange, player.getPlayerRectangle()) || isAttackOngoing()){
            // save the position to seek when it is first "seen" and project on the circle
            if(currentState != State.ATTACK){
                attackTarget = player.getPosition().cpy();
                Vector2 vectorToCenter = attackTarget.sub(position);
                attackTarget = vectorToCenter.nor().scl(575).add(position);
            }
            // accelerate to match the dash animation and seek the position of the player when it was first "seen"
            if(isAttackTiming()){
                if(!enemyRectangle.overlaps(player.getPlayerRectangle()) && trollChargeRange.contains(position)) {
                    maxSpeed = 3000;
                    maxForce = 500;
                    currentSteeringBehavior = seek(attackTarget.cpy(), deltaTime);
                    applySteeringBehaviour(currentSteeringBehavior, deltaTime);
                    maxForce = 2;
                    maxSpeed = 120;
                }
            }
            currentState = State.ATTACK;
        } else{
            if(!enemyRectangle.overlaps(player.getPlayerRectangle())){
                currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior, deltaTime);
            } else {
                // don't let player overlap minion
                noOverlappingWithPlayer(player);
            }
            currentState = State.WALKING;
        }

        // troll dies
        if (health <= 0)
            currentState = State.DEAD;
    }

    private boolean isAttackOngoing(){
        return currentState == State.ATTACK && !attackAnimation.animation.isAnimationFinished(stateTimer);
    }

    // get range for the attack charge (for debug purposes)
    public Circle getTrollChargeRange() {
        return trollChargeRange;
    }

}
