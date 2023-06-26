package com.mygdx.game.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class Potion {
    private final Texture potion;
    private final boolean isArmor;
    private final float healing;
    private final float x;
    private final float y;
    private final Sound pickupSoundEffect;

    public Potion(boolean isArmor, float x, float y, float healing, Texture potionTexture, String soundEffectPath){
        this.potion = potionTexture;
        this.isArmor = isArmor;
        this.healing = healing;
        this.x = x;
        this.y = y;
        this.pickupSoundEffect = Gdx.audio.newSound(Gdx.files.internal(soundEffectPath));
    }
    public void draw(SpriteBatch spriteBatch){
        spriteBatch.draw(potion, x, y);
    }
    public Rectangle getBounds(){
        return new Rectangle((int)x, (int)y, potion.getWidth(), potion.getHeight());
    }
    public boolean isArmor(){
        return isArmor;
    }
    public float getHealing(){
        return healing;
    }
    public Sound getPickupSoundEffect(){ return pickupSoundEffect; }
}
