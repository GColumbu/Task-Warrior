package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.enemies.EnemyAnimation;

public class EnemyTextures {
    public TextureRegion idleTextureRegion;
    public EnemyAnimation walkingAnimation;
    public EnemyAnimation walkingDamageAnimation;
    public EnemyAnimation attackAnimation;
    public EnemyAnimation attackDamageAnimation;
    public EnemyAnimation dyingAnimation;

    public EnemyTextures(TextureRegion idleTextureRegion, EnemyAnimation walkingAnimation, EnemyAnimation walkingDamageAnimation,
                            EnemyAnimation attackAnimation, EnemyAnimation attackDamageAnimation, EnemyAnimation dyingAnimation){
        this.idleTextureRegion = idleTextureRegion;
        this.walkingAnimation = walkingAnimation;
        this.walkingDamageAnimation = walkingDamageAnimation;
        this.attackAnimation = attackAnimation;
        this.attackDamageAnimation = attackDamageAnimation;
        this.dyingAnimation = dyingAnimation;
    }
    public void dispose(){
        idleTextureRegion.getTexture().dispose();
        walkingAnimation.dispose();
        walkingDamageAnimation.dispose();
        attackAnimation.dispose();
        attackDamageAnimation.dispose();
        dyingAnimation.dispose();
    }
}
