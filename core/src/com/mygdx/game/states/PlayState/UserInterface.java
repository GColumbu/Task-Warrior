package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class UserInterface {
    // ui template
    private final Texture userInterface;
    //first attack icon
    private final AttackIcon firstAttack;
    //second attack icon
    private final AttackIcon secondAttack;
    //third attack icon
    private final AttackIcon thirdAttack;

    protected UserInterface(String userInterfaceFilePath, Texture firstAttackIcon, Texture secondAttackIcon, Texture thirdAttackIcon){
        this.userInterface = new Texture(userInterfaceFilePath);
        this.firstAttack = new AttackIcon(firstAttackIcon, 19, 5);
        this.secondAttack = new AttackIcon(secondAttackIcon, 59, 5);
        this.thirdAttack = new AttackIcon(thirdAttackIcon, 99, 5);
    }

    protected Vector2 getUIPosition(OrthographicCamera camera){
        return new Vector2(camera.position.x - (float)(userInterface.getWidth() / 2.0) , 0);
    }

    protected void draw(SpriteBatch spriteBatch, OrthographicCamera camera){
        float x = getUIPosition(camera).x;
        float y = getUIPosition(camera).y;
        spriteBatch.draw(userInterface, x, y);
        spriteBatch.draw(firstAttack.attackIconTexture, x + firstAttack.xOffset, y + secondAttack.yOffset);
        spriteBatch.draw(secondAttack.attackIconTexture, x + secondAttack.xOffset, y + secondAttack.yOffset);
        spriteBatch.draw(thirdAttack.attackIconTexture, x + thirdAttack.xOffset, y + thirdAttack.yOffset);
    }

}
