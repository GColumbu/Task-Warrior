package com.mygdx.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class Potion {
    private Texture potion;
    private boolean isArmor;
    private float healing;
    private float x;
    private float y;

    public Potion(boolean armor, float x, float y, float healing){
        if(armor){
            potion = new Texture("assets/play screen/potions/armor_potion.png");
        } else {
            potion = new Texture("assets/play screen/potions/health_potion.png");
        }
        this.isArmor = armor;
        this.healing = healing;
        this.x = x;
        this.y = y;
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

    public void dispose(){
        potion.dispose();
    }
}
