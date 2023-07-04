package com.mygdx.game.states.UpgradesState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.states.PlayerStatistic;
import com.mygdx.game.states.TemporaryAccountDetails;

public class AbilityUpgrade extends Upgrade{
    private PlayerStatistic statistic;
    private TemporaryAccountDetails accountDetails;
    private Stage stage;
    private Label upgradeName;
    private Button purchaseButton;
    private final Label.LabelStyle labelStyle;
    private Image abilityUpgradeContainer;
    private int currentIndex;
    private final Color magicColor = new Color(100/255f, 240/255f, 195/255f, 1);
    private final Color magicColorChecked = new Color(42/255f, 167/255f, 216/255f, 1);

    protected AbilityUpgrade(PlayerStatistic statistic, TemporaryAccountDetails accountDetails, Stage stage, Label.LabelStyle labelStyle, int currentIndex){
        this.statistic = statistic;
        this.accountDetails = accountDetails;
        this.labelStyle = labelStyle;
        this.stage = stage;
        this.currentIndex = currentIndex;

        configureAbilityUpgradeContainer(currentIndex);
        configureLabel(currentIndex);
        configureSubmitButton();
    }
    @Override
    protected void update(){
        for(int i=0; i<4; i++){
            configureUpgradeMiniContainer(i, i < statistic.getUpgradeLevel());
        }
    }

    private void configureAbilityUpgradeContainer(int i){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(magicColor);
        pixmap.fill();
        this.abilityUpgradeContainer = new Image(new Texture(pixmap));
        pixmap.dispose();
        this.abilityUpgradeContainer.setSize(85, 85);
        this.abilityUpgradeContainer.setPosition(1328 + i*150, 320);
        stage.addActor(this.abilityUpgradeContainer);
    }

    private void configureSubmitButton(){
        // configure button
        Pixmap pixmapUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapUp.setColor(Color.GRAY);
        pixmapUp.fill();
        TextureRegionDrawable upButtonTexture = new TextureRegionDrawable(new Texture(pixmapUp));
        pixmapUp.dispose();

        Pixmap pixmapChecked = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapChecked.setColor(magicColor);
        pixmapChecked.fill();
        TextureRegionDrawable checkedButtonTexture = new TextureRegionDrawable(new Texture(pixmapChecked));
        pixmapChecked.dispose();

        Button.ButtonStyle submitButtonStyle = new Button.ButtonStyle();
        submitButtonStyle.up = upButtonTexture;
        submitButtonStyle.down = checkedButtonTexture;

        this.purchaseButton = new Button(submitButtonStyle);
        this.purchaseButton.setPosition(1320 + currentIndex*150, 215);
        this.purchaseButton.setSize(100, 50);
        this.purchaseButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(accountDetails.getCoins() >= statistic.getUpgradeCost()){
                    accountDetails.setCoins(accountDetails.getCoins() - statistic.getUpgradeCost());
                    statistic.setUpgradeLevel(statistic.getUpgradeLevel() + 1);
                    statistic.setValue(statistic.getValue() + statistic.getUpgradeValue());
                }
            }
        } );

        Label submitLabel = new Label(statistic.getUpgradeCost().toString(), labelStyle);

        this.purchaseButton.add(submitLabel);
        stage.addActor(this.purchaseButton);
    }

    private void configureLabel(int i){
        this.upgradeName = new Label(statistic.getName(), labelStyle);
        this.upgradeName.setPosition(1361 + i*150, 340);
        stage.addActor(this.upgradeName);
    }

    private void configureUpgradeMiniContainer(int i, boolean isUpgradePurchased){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        if(isUpgradePurchased)
            pixmap.setColor(magicColor);
        else
            pixmap.setColor(Color.GRAY);
        pixmap.fill();
        Image taskContainer = new Image(new Texture(pixmap));
        pixmap.dispose();
        taskContainer.setSize(10, 30);
        taskContainer.setPosition(1335 + currentIndex*150 + i*20, 280);
        stage.addActor(taskContainer);
    }
}
