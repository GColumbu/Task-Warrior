package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.states.PlayState.PlayState;
import com.mygdx.game.states.TasksState.TasksState;

public class MenuState implements Screen {
    // constants
    private static final String TITLE = "Task Warrior";
    private static final String PLAY = "Play";
    private static final String TASKS = "Tasks";
    private static final String CHAMPIONS = "Champions";

    // game utils
    private TaskWarrior game;
    private final Viewport viewport;
    private TemporaryAccountDetails accountDetails;
    private final Stage stage;
    private final Sound selectSound;
    private final Music mainMenuMusic;
    private final Color magicColor;

    // Buttons
        // Play Button
    private TextureRegionDrawable playButtonTexture;
    private Button playButton;
    private Button.ButtonStyle playButtonStyle;

        // Tasks Button
    private TextureRegionDrawable tasksButtonTexture;
    private Button tasksButton;
    private Button.ButtonStyle tasksButtonStyle;

        // Champions Button
    private TextureRegionDrawable championsButtonTexture;
    private Button championsButton;
    private Button.ButtonStyle championsButtonStyle;

    // GdxFreeType
    private final Label.LabelStyle labelStyle;
    private final FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public MenuState(TaskWarrior game, TemporaryAccountDetails accountDetails) {
        // game utils
        this.game = game;
        this.game.batch = new SpriteBatch();
        this.stage = new Stage();
        this.accountDetails = accountDetails;
        this.viewport = new ScreenViewport();
        this.magicColor = new Color(100/255f, 240/255f, 195/255f, 1);
        this.selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/select.mp3"));
        this.mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/music/main_menu_music.mp3"));
        this.mainMenuMusic.setLooping(true);
        this.mainMenuMusic.play();
        this.mainMenuMusic.setVolume(0.03f);

        // GdxFreeType
        this.labelStyle = new Label.LabelStyle();
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/pixel_font.ttf"));
        this.parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // configure screen objects
        configureBackground();
        configureTitle();
        configureButtons();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        stage.dispose();
    }


    private void render(){
        stage.draw();
    }

    private void configureBackground(){
        Image background = new Image(new Texture("home_background.jpg"));
        background.setPosition(0, 0);
        stage.addActor(background);
    }

    // Title
    private void configureTitle(){
        labelStyle.font = getTitleStyle();

        Label titleLabel = new Label(TITLE, labelStyle);
        titleLabel.setSize(100, 100);
        titleLabel.setAlignment(Align.center);
        titleLabel.setPosition(280, Gdx.graphics.getHeight() - 110);
        stage.addActor(titleLabel);
    }

    // Buttons
    private void configureButtons(){
        configurePlayButton();
        configureTasksButton();
        configureChampionsButton();
    }

        // Play Button
    private void configurePlayButton(){
        // button
        playButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button.png")));
        playButtonStyle = new Button.ButtonStyle();
        playButtonStyle.up = playButtonTexture;
        playButton = new Button(playButtonStyle);
        playButton.setPosition(20, Gdx.graphics.getHeight() - 200);
        playButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectSound.setVolume(selectSound.play(), 0.1f);
                mainMenuMusic.stop();
                game.setScreen(new PlayState(game, accountDetails));
            }
        } );

        // label
        labelStyle.font = getRedirectButtonsStyle();
        Label playLabel = new Label(PLAY, labelStyle);
        playButton.add(playLabel);
        stage.addActor(playButton);
    }

    private void configureTasksButton(){
        // button
        tasksButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button.png")));
        tasksButtonStyle = new Button.ButtonStyle();
        tasksButtonStyle.up = tasksButtonTexture;
        tasksButton = new Button(tasksButtonStyle);
        tasksButton.setPosition(20, Gdx.graphics.getHeight() - 265);
        tasksButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectSound.setVolume(selectSound.play(), 0.1f);
                mainMenuMusic.stop();
                game.setScreen(new TasksState(game, accountDetails));
            }
        } );

        // label
        labelStyle.font = getRedirectButtonsStyle();
        Label tasksLabel = new Label(TASKS, labelStyle);
        tasksButton.add(tasksLabel);
        stage.addActor(tasksButton);
    }

    private void configureChampionsButton(){
        // button
        championsButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button.png")));
        championsButtonStyle = new Button.ButtonStyle();
        championsButtonStyle.up = championsButtonTexture;
        championsButton = new Button(championsButtonStyle);
        championsButton.setPosition(20, Gdx.graphics.getHeight() - 330);
        championsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectSound.setVolume(selectSound.play(), 0.1f);
                //game.setScreen(new PlayState(game, bestScore));
            }
        } );

        // label
        labelStyle.font = getRedirectButtonsStyle();
        Label championLabel = new Label(CHAMPIONS, labelStyle);
        championsButton.add(championLabel);
        stage.addActor(championsButton);
    }

    // Styles
    private BitmapFont getTitleStyle(){
        parameter.size = 50;
        parameter.borderWidth = 3;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }

    private BitmapFont getRedirectButtonsStyle(){
        parameter.size = 20;
        parameter.borderWidth = 3;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }
    private BitmapFont getRedirectButtonsHoverStyle(){
        parameter.size = 20;
        parameter.borderWidth = 3;
        parameter.color = new Color(100/255f, 240/255f, 195/255f, 1);
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }

    // Hover Logic
    private void addHoverLogic(){
        addHoverLogicForButton(playButton);
        addHoverLogicForButton(tasksButton);
        addHoverLogicForButton(championsButton);
    }

    private void addHoverLogicForButton(Button button){
        if (button.isOver()){
            championsButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button_hover.png")));
            button.getStyle().up = championsButtonTexture;
        } else {
            championsButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button.png")));
            button.getStyle().up = championsButtonTexture;
        }
        if(button.isOver() && button.getX() < 60){
            button.setPosition(button.getX() + 3, button.getY());
        } else if(!button.isOver() && button.getX() > 20){
            button.setPosition(button.getX() - 3, button.getY());
        }
    }

    private void update(float deltaTime){
        viewport.getCamera().update();
        stage.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        stage.act(deltaTime);
        addHoverLogic();
    }
}
