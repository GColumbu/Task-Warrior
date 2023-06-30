package com.mygdx.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.players.PlayerChampion;
import com.mygdx.game.players.garen.Garen;
import com.mygdx.game.states.PlayState.EnemyTextures;

import java.util.List;

public class Minion extends Enemy {
    //constants
    private final static int MINION_MAX_SPEED = 150;
    private final static int MINION_MAX_FORCE = 20;
    private final static int MINION_HEALTH = 300;
    private final static float MINION_DAMAGE = 1F;
    private final static float ATTACK_COOLDOWN = 0F;

    public Minion(int x, int y, EnemyTextures enemyTextures) {
        super(x, y, MINION_MAX_SPEED, MINION_MAX_FORCE, MINION_HEALTH, MINION_DAMAGE, ATTACK_COOLDOWN);
        currentRegion = enemyTextures.idleTextureRegion;
        currentState = State.WALKING;
        this.idleTextureRegion = enemyTextures.idleTextureRegion;
        this.walkingAnimation = enemyTextures.walkingAnimation;
        this.walkingDamageAnimation = enemyTextures.walkingDamageAnimation;
        this.attackAnimation = enemyTextures.attackAnimation;
        this.attackDamageAnimation = enemyTextures.attackDamageAnimation;
        this.dyingAnimation = enemyTextures.dyingAnimation;
        attackSoundEffect = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/minion/minion_attack.mp3"));
        dyingSoundEffect = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/minion/minion_death.mp3"));
    }

    @Override
    public void update(PlayerChampion player, float deltaTime) {
        //applySteeringBehaviour(flee(player.getPosition().cpy(), deltaTime));
        //applySteeringBehaviour(pursue(player, deltaTime));
        setCurrentRegion(getFrame(deltaTime));
        setEnemyRectangle(new Rectangle(getWalkingRelativePosition().x, getWalkingRelativePosition().y, idleTextureRegion.getRegionWidth(), idleTextureRegion.getRegionHeight()));
        setEnemySenseRange(new Circle(position.x, position.y, 100));
        calculateDamage(player, 5);
        addSoundEffects();
    }

    @Override
    public void move(PlayerChampion player, List<Enemy> minions, float deltaTime) {
        separation(getNearbyEnemies(minions));
        addBehavior(player, deltaTime);
    }

    @Override
    protected void addBehavior(PlayerChampion player, float deltaTime) {
        // improvement variables
        boolean isColliding = enemyRectangle.overlaps(player.getPlayerRectangle());
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
            else if (!isColliding) {
                currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior, deltaTime);
                currentState = State.WALKING;
            }
        }
        else {
            // apply seek steering behaviour if collision not detected
            if (!isColliding) {
                currentSteeringBehavior = seek(player.getPosition().cpy(), deltaTime);
                applySteeringBehaviour(currentSteeringBehavior, deltaTime);
                currentState = State.WALKING;
            }
        }

        // don't let player overlap minion
        if (isColliding) {
            noOverlappingWithPlayer(player);
            currentState = State.ATTACK;
        }

        // minion dies
        if (health <= 0){
            currentState = State.DEAD;
        }
    }

    @Override
    public Rectangle getAttackRange(){
        return new Rectangle(getAttackRelativePosition().x, getAttackRelativePosition().y, getSprite().getRegionWidth(), getSprite().getRegionHeight());
    }

    @Override
    protected boolean isAttackTiming() {
        return true;
    }

    private void addSoundEffects(){
        if  (currentState == State.ATTACK && attackAnimation.getKeyFrameIndex(stateTimer) == 1){
            attackSoundEffect.setVolume(attackSoundEffect.play(), 0.03f);
        }
        if (currentState == State.DEAD && dyingAnimation.getKeyFrameIndex(stateTimer) == 0){
            dyingSoundEffect.setVolume(dyingSoundEffect.play(), 0.03f);
        }
    }
}
