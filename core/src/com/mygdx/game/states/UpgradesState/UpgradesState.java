package com.mygdx.game.states.UpgradesState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.TemporaryAccountDetails;

import java.util.ArrayList;
import java.util.List;

public class UpgradesState implements Screen {
    private static final String BACK = "MAIN MENU";
    private static final String GAREN = "GAREN";
    private static final String DESCRIPTION = "THE SPINNING WARRIOR";
    // game utils
    private TaskWarrior game;
    private final Viewport viewport;
    private TemporaryAccountDetails accountDetails;
    private final Stage stage;

    // GdxFreeType
    private final Label.LabelStyle labelStyle;
    private final FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    // game elements
    private Image coins;
    private Button backToMainMenu;
    private Image upgradesContainer;
    private Label coinsLabel;
    private List<Upgrade> upgrades;
    private final Color magicColor = new Color(100/255f, 240/255f, 195/255f, 1);

    public UpgradesState(TaskWarrior game, TemporaryAccountDetails accountDetails) {
        // game utils
        this.game = game;
        this.game.batch = new SpriteBatch();
        this.stage = new Stage();
        this.accountDetails = accountDetails;
        this.viewport = new ScreenViewport();

        // GdxFreeType
        this.labelStyle = new Label.LabelStyle();
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/pixel_font.ttf"));
        this.parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // configuration
        configureCoinsSection();
        configureBackToMainMenuButton();
        configureUpgradeContainer();
        configureChampionDescription();
        upgrades = configureUpgrades();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        draw();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void update(float deltaTime){
        viewport.getCamera().update();
        stage.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        // make screen black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(deltaTime);
        addHoverLogicForButton();
        updateCoinsLabel();
        for(Upgrade statisticUpgrade : upgrades){
            statisticUpgrade.update();
        }
    }

    private void draw(){
        stage.draw();
    }

    private void configureCoinsSection(){
        this.coins = new Image(new Texture("assets/coins.png"));
        coins.setPosition(200, 200);
        labelStyle.font = getSubTitleStyle();
        this.coinsLabel = new Label(accountDetails.getCoins().toString(), labelStyle);
        this.coinsLabel.setSize(100, 100);
        this.coinsLabel.setAlignment(Align.left);
        this.coinsLabel.setPosition(280, 185);

        stage.addActor(coins);
        stage.addActor(coinsLabel);
    }

    private void updateCoinsLabel(){
        coinsLabel.setText(accountDetails.getCoins().toString());
    }

    private void configureChampionDescription(){
        labelStyle.font = getTitleStyle();
        Label garen = new Label(GAREN, labelStyle);
        garen.setSize(100, 100);
        garen.setAlignment(Align.left);
        garen.setPosition(1300, 100);

        labelStyle.font = getDescriptionStyle();
        Label description = new Label(DESCRIPTION, labelStyle);
        description.setSize(100, 100);
        description.setAlignment(Align.left);
        description.setPosition(1300, 40);
        stage.addActor(garen);
        stage.addActor(description);
    }

    private List<Upgrade> configureUpgrades(){
        List<Upgrade> upgrades = new ArrayList<>();
        labelStyle.font = getUpgradesLabelStyle();
        for(int i=0; i<6; i++){
            StatisticUpgrade currentStatisticUpgrade = new StatisticUpgrade(accountDetails.getStatistics().get(i), accountDetails, stage, labelStyle, 1320, 980 - i * 100, i);
            upgrades.add(currentStatisticUpgrade);
        }
        for(int i=6; i<9; i++){
            AbilityUpgrade currentAbilityUpgrade = new AbilityUpgrade(accountDetails.getStatistics().get(i), accountDetails, stage, labelStyle, i-6);
            upgrades.add(currentAbilityUpgrade);
        }

        return upgrades;
    }

    private void configureBackToMainMenuButton(){
        // button
        TextureRegionDrawable buttonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button.png")));
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = buttonTexture;
        this.backToMainMenu = new Button(buttonStyle);
        this.backToMainMenu.setPosition(30, Gdx.graphics.getHeight() - 85);
        this.backToMainMenu.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuState(game, accountDetails));
            }
        } );

        // label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = getMainMenuStyle();
        Label championLabel = new Label(BACK, labelStyle);
        this.backToMainMenu.add(championLabel);
        stage.addActor(backToMainMenu);
    }

    private void addHoverLogicForButton(){
        TextureRegionDrawable textureRegionDrawable;
        if (backToMainMenu.isOver()){
            textureRegionDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button_hover.png")));
            backToMainMenu.getStyle().up = textureRegionDrawable;
        } else {
            textureRegionDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button.png")));
            backToMainMenu.getStyle().up = textureRegionDrawable;
        }
    }

    private void configureUpgradeContainer(){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BROWN);
        pixmap.fill();
        this.upgradesContainer = new Image(new Texture(pixmap));
        pixmap.dispose();
        this.upgradesContainer.setSize(440, 830);
        this.upgradesContainer.setPosition(1300, 200);
        stage.addActor(this.upgradesContainer);
    }

    protected BitmapFont getSubTitleStyle(){
        parameter.size = 25;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetY = 2;
        parameter.shadowOffsetX = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }

    protected BitmapFont getDescriptionStyle(){
        parameter.size = 17;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetY = 2;
        parameter.shadowOffsetX = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }

    protected BitmapFont getTitleStyle(){
        parameter.size = 40;
        parameter.borderWidth = 3;
        parameter.borderColor = Color.BLACK;
        parameter.color = magicColor;
        parameter.shadowOffsetY = 2;
        parameter.shadowOffsetX = 2;
        parameter.shadowColor = Color.GRAY;
        return generator.generateFont(parameter);
    }

    protected BitmapFont getUpgradesLabelStyle(){
        parameter.size = 17;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetY = 1;
        parameter.shadowOffsetX = 1;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }
    protected BitmapFont getMainMenuStyle(){
        parameter.size = 20;
        parameter.borderWidth = 3;
        parameter.borderColor = Color.BLACK;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetY = 2;
        parameter.shadowOffsetX = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }
}
