package com.mygdx.game.states.PlayState.UI;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.players.PlayerChampion;


public class UserInterface {
    // UI template
    private final Texture userInterface;

    // health bar
    private final HealthBar healthBar;

    // armor bar
    private final ArmorBar armorBar;

    // first attack utils
    private final AttackIcon firstAttack;
    private final AbilityCooldownBar firstAbilityCooldown;

    // second attack utils
    private final AttackIcon secondAttack;
    private final AbilityCooldownBar secondAbilityCooldown;

    // third attack utils
    private final AttackIcon thirdAttack;
    private final AbilityCooldownBar thirdAbilityCooldown;



    public UserInterface(String userInterfaceFilePath,PlayerChampion player){
        this.userInterface = new Texture(userInterfaceFilePath);
        this.firstAttack = new AttackIcon(player.getQBasicAnimation().getAttackIcon(), 45, 5);
        this.firstAbilityCooldown = new AbilityCooldownBar(player.getQBasicAnimation().getCooldownDuration(), 28, 4, 10);
        this.secondAttack = new AttackIcon(player.getWBasicAnimation().getAttackIcon(), 117, 4);
        this.secondAbilityCooldown = new AbilityCooldownBar(player.getWBasicAnimation().getCooldownDuration(), 100, 4, 10);
        this.thirdAttack = new AttackIcon(player.getEBasicAnimation().getAttackIcon(), 189, 4);
        this.thirdAbilityCooldown = new AbilityCooldownBar(player.getEBasicAnimation().getCooldownDuration(), 174, 4, 10);
        this.healthBar = new HealthBar(player.getMaxHealth());
        this.armorBar = new ArmorBar(player.getMaxArmor());
    }

    protected Vector2 getUIPosition(OrthographicCamera camera){
        return new Vector2(camera.position.x - (float)(userInterface.getWidth() / 2.0) * camera.zoom, camera.position.y - camera.viewportHeight/2 * camera.zoom);
    }

    public void draw(SpriteBatch spriteBatch, OrthographicCamera camera, PlayerChampion player){
        Vector2 position = getUIPosition(camera);

        // draw template
        spriteBatch.draw(userInterface, position.x, position.y, userInterface.getWidth() * camera.zoom, userInterface.getHeight() * camera.zoom);

        // draw Q ability icon and cooldown
        spriteBatch.draw(firstAttack.attackIconTexture, position.x + firstAttack.xOffset * camera.zoom, position.y + secondAttack.yOffset * camera.zoom, firstAttack.attackIconTexture.getWidth() * camera.zoom, firstAttack.attackIconTexture.getHeight() * camera.zoom);
        firstAbilityCooldown.draw(spriteBatch, position.x, position.y, player.getQBasicAnimation().getCooldownStateTimer(), camera.zoom);

        // draw W ability icon and cooldown
        spriteBatch.draw(secondAttack.attackIconTexture, position.x + secondAttack.xOffset * camera.zoom, position.y + secondAttack.yOffset * camera.zoom, secondAttack.attackIconTexture.getWidth() * camera.zoom, secondAttack.attackIconTexture.getHeight() * camera.zoom);
        secondAbilityCooldown.draw(spriteBatch, position.x, position.y, player.getWBasicAnimation().getCooldownStateTimer(), camera.zoom);

        // draw E ability icon and cooldown
        spriteBatch.draw(thirdAttack.attackIconTexture, position.x + thirdAttack.xOffset * camera.zoom, position.y + thirdAttack.yOffset * camera.zoom, thirdAttack.attackIconTexture.getWidth() * camera.zoom, thirdAttack.attackIconTexture.getHeight() * camera.zoom);
        thirdAbilityCooldown.draw(spriteBatch, position.x, position.y, player.getEBasicAnimation().getCooldownStateTimer(), camera.zoom);

        // draw health and armor bar
        healthBar.draw(spriteBatch, position.x, position.y, player.getHealth(), camera.zoom);
        armorBar.draw(spriteBatch, position.x, position.y, player.getArmor(), camera.zoom);
    }

    public void dispose(){
        userInterface.dispose();
        firstAttack.dispose();
        secondAttack.dispose();
        thirdAttack.dispose();
    }

}
