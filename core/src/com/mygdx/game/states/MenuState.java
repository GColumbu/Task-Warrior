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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

public class MenuState implements Screen {
    // constants
    private static final String TITLE = "Task Warrior";
    private static final String PLAY = "Play";
    private static final String TASKS = "Tasks";
    private static final String CHAMPIONS = "Champions";

    // game utils
    private TaskWarrior game;
    private final Viewport viewport;
    private Integer bestScore;
    private final Stage stage;
    private final Sound selectSound;
    private final Music mainMenuMusic;

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

    public MenuState(TaskWarrior game, Integer bestScore) {
        // game utils
        this.game = game;
        this.game.batch = new SpriteBatch();
        this.stage = new Stage();
        this.bestScore = bestScore;
        this.viewport = new ScreenViewport();
        this.selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/select.mp3"));
        this.mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/main_menu_music.mp3"));
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
        viewport.getCamera().update();
        stage.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        stage.act(deltaTime);
        addHoverLogic();
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
        titleLabel.setPosition(280, 940);
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
        playButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/game over screen/main_menu.png")));
        playButtonStyle = new Button.ButtonStyle();
        playButtonStyle.up = playButtonTexture;
        playButton = new Button(playButtonStyle);
        playButton.setPosition(20, 850);
        playButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectSound.setVolume(selectSound.play(), 0.1f);
                mainMenuMusic.stop();
                game.setScreen(new PlayState(game, bestScore));
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
        tasksButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/game over screen/try_again.png")));
        tasksButtonStyle = new Button.ButtonStyle();
        tasksButtonStyle.up = tasksButtonTexture;
        tasksButton = new Button(tasksButtonStyle);
        tasksButton.setPosition(20, 785);
        tasksButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectSound.setVolume(selectSound.play(), 0.1f);
                //game.setScreen(new PlayState(game, bestScore));
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
        championsButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/game over screen/main_menu.png")));
        championsButtonStyle = new Button.ButtonStyle();
        championsButtonStyle.up = championsButtonTexture;
        championsButton = new Button(championsButtonStyle);
        championsButton.setPosition(20, 720);
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

    // Hover Logic
    private void addHoverLogic(){
        addHoverLogicForButton(playButton);
        addHoverLogicForButton(tasksButton);
        addHoverLogicForButton(championsButton);
    }

    private void addHoverLogicForButton(Button button){
        if(button.isOver() && button.getX() < 60){
            button.setPosition(button.getX() + 3, button.getY());
        } else if(!button.isOver() && button.getX() > 20){
            button.setPosition(button.getX() - 3, button.getY());
        }
    }
}
