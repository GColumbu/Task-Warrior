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
import com.mygdx.game.states.PlayerStatistic;
import com.mygdx.game.states.TemporaryAccountDetails;

public class StatisticUpgrade extends Upgrade{
    private PlayerStatistic statistic;
    private TemporaryAccountDetails accountDetails;
    private Stage stage;
    private int currentIndex;
    private Label upgradeName;
    private Button purchaseButton;
    private final Label.LabelStyle labelStyle;
    private final Color magicColor = new Color(100/255f, 240/255f, 195/255f, 1);
    private final Color magicColorChecked = new Color(42/255f, 167/255f, 216/255f, 1);


    protected StatisticUpgrade(PlayerStatistic statistic, TemporaryAccountDetails accountDetails, Stage stage, Label.LabelStyle labelStyle, int x, int y, int currentIndex){
        this.statistic = statistic;
        this.stage = stage;
        this.labelStyle = labelStyle;
        this.currentIndex = currentIndex;
        this.accountDetails = accountDetails;

        configureLabel(x, y);
        configureSubmitButton();
        configureCoinImage();
    }

    private void configureLabel(int x, int y){
        this.upgradeName = new Label(statistic.getName() + ":", labelStyle);
        this.upgradeName.setPosition(x, y);
        stage.addActor(upgradeName);
    }

    @Override
    protected void update(){
        for(int i=0; i<4; i++){
            configureUpgradeMiniContainer(i, i < statistic.getUpgradeLevel());
        }
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
        taskContainer.setSize(20, 50);
        taskContainer.setPosition(1320 + i*45, 930 - 100*currentIndex);
        stage.addActor(taskContainer);
    }

    private void configureSubmitButton(){
        // configure button
        Pixmap pixmapUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapUp.setColor(magicColor);
        pixmapUp.fill();
        TextureRegionDrawable upButtonTexture = new TextureRegionDrawable(new Texture(pixmapUp));
        pixmapUp.dispose();

        Pixmap pixmapChecked = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapChecked.setColor(magicColorChecked);
        pixmapChecked.fill();
        TextureRegionDrawable checkedButtonTexture = new TextureRegionDrawable(new Texture(pixmapChecked));
        pixmapChecked.dispose();

        Button.ButtonStyle submitButtonStyle = new Button.ButtonStyle();
        submitButtonStyle.up = upButtonTexture;
        submitButtonStyle.down = checkedButtonTexture;

        this.purchaseButton = new Button(submitButtonStyle);
        this.purchaseButton.setPosition(1485, 930 - 100*currentIndex);
        this.purchaseButton.setSize(240, 50);
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

        Label submitLabel = new Label("BUY ( " + statistic.getUpgradeCost().toString() + "        )", labelStyle);

        this.purchaseButton.add(submitLabel);
        stage.addActor(this.purchaseButton);
    }

    private void configureCoinImage(){
        Image coins = new Image(new Texture("assets/coins.png"));
        coins.setSize(40, 40);
        coins.setPosition(1640, 935 - 100*currentIndex);
        stage.addActor(coins);
    }



}
