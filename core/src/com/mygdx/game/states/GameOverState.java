package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.mygdx.game.states.PlayState.PlayState;

public class GameOverState implements Screen {

    // game utils
    private TaskWarrior game;
    private Stage stage;
    private final Viewport viewport;
    private final Music gameOverMusic;
    private final Sound selectSound;
    private final Color redColor;
    // textures
    private Image background;

        // Main Menu Button
    private TextureRegionDrawable mainMenuButtonTexture;
    private Button mainMenuButton;
    private Button.ButtonStyle mainMenuButtonStyle;

        // Try Again Button
    private TextureRegionDrawable tryAgainButtonTexture;
    private Button tryAgainButton;
    private Button.ButtonStyle tryAgainButtonStyle;

    // GdxFreeType
    private Label.LabelStyle labelStyle;
    private final FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    // Scores
    private Integer bestScore;
    private Integer score;
    private static final String MAIN_MENU_LABEL = "Main Menu";
    private static final String TRY_AGAIN_LABEL = "Try Again";
    private static final String YOU_LOST = "YOU LOST";
    private static final String SKULLS = "SKULLS";
    private static final String HIGHSCORE = "HIGHSCORE!";
    private static final String THIS_RUN = "This Run";
    private static final String BEST_RUN = "Best Run";

    public GameOverState(TaskWarrior game, Integer score, Integer bestScore) {
        // game utils
        this.game = game;
        this.stage = new Stage();
        this.score = score;
        this.bestScore = bestScore;
        this.redColor = new Color(117/255f, 25/255f, 25/255f, 1);

        // buttons
        configureBackground();
        configureMainMenuButton();
        configureTryAgainButton();


        // GdxFreeType
        this.labelStyle = new Label.LabelStyle();
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/pixel_font.ttf"));
        this.parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        configureHighscoreLabel();
        configureRedirectButtonsLabel();
        configureYouLostLabel();
        configureSkullsLabel();
        configureScoreLabel();

        this.viewport = new ScreenViewport();
        this.gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/game_over_music.mp3"));
        this.gameOverMusic.setLooping(true);
        this.gameOverMusic.play();
        this.gameOverMusic.setVolume(0.03f);
        this.selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/select.mp3"));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        stage.act(deltaTime);
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
        game.batch.dispose();
        stage.dispose();
    }

    private void render(){
        stage.draw();
    }

    private void configureBackground(){
        this.background = new Image(new Texture("assets/game over screen/background_sword.png"));
        this.background.setPosition(0, 0);
        this.stage.addActor(this.background);
    }

    private void configureMainMenuButton(){
        this.mainMenuButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/game over screen/main_menu.png")));
        mainMenuButtonStyle = new Button.ButtonStyle();
        mainMenuButtonStyle.up = mainMenuButtonTexture;
        mainMenuButton = new Button(mainMenuButtonStyle);
        mainMenuButton.setPosition(680, 80);
        mainMenuButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectSound.setVolume(selectSound.play(), 0.1f);
                gameOverMusic.stop();
                game.setScreen( new MenuState(game, bestScore));
            }
        } );
        stage.addActor(mainMenuButton);
    }

    private void configureTryAgainButton(){
        this.tryAgainButtonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/game over screen/try_again.png")));
        tryAgainButtonStyle = new Button.ButtonStyle();
        tryAgainButtonStyle.up = tryAgainButtonTexture;
        tryAgainButton = new Button(tryAgainButtonStyle);
        tryAgainButton.setPosition(980, 80);
        tryAgainButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectSound.setVolume(selectSound.play(), 0.1f);
                gameOverMusic.stop();
                game.setScreen( new PlayState(game, bestScore));
            }
        } );
        stage.addActor(tryAgainButton);
    }

    private void configureHighscoreLabel(){
        labelStyle.font = getSkullsStyle();

        Label highScoreLabel = new Label(HIGHSCORE, labelStyle);
        highScoreLabel.setSize(80, 80);
        highScoreLabel.setAlignment(Align.center);
        highScoreLabel.setPosition(920, 330);

        if(score > bestScore){
            stage.addActor(highScoreLabel);
            bestScore = score;
        }
    }

    private void configureRedirectButtonsLabel(){
        labelStyle.font = getRedirectAndScoreButtonsStyle();

        Label mainMenuLabel = new Label(MAIN_MENU_LABEL, labelStyle);
        Label tryAgainLabel = new Label(TRY_AGAIN_LABEL, labelStyle);

        mainMenuButton.add(mainMenuLabel);
        tryAgainButton.add(tryAgainLabel);
    }
    private void configureYouLostLabel(){
        labelStyle.font = getYouLostStyle();

        Label scoreLabel = new Label(YOU_LOST, labelStyle);
        scoreLabel.setSize(100, 100);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setPosition(900, 400);
        stage.addActor(scoreLabel);
    }
    private void configureSkullsLabel(){
        labelStyle.font = getSkullsStyle();

        Label skullsLabel = new Label(SKULLS, labelStyle);
        skullsLabel.setSize(80, 80);
        skullsLabel.setAlignment(Align.center);
        skullsLabel.setPosition(920, 260);
        stage.addActor(skullsLabel);
    }
    private void configureScoreLabel(){
        labelStyle.font = getRedirectAndScoreButtonsStyle();

        Label thisRunLabel = new Label(THIS_RUN, labelStyle);
        thisRunLabel.setSize(80, 80);
        thisRunLabel.setAlignment(Align.center);
        thisRunLabel.setPosition(760, 200);

        Label bestRunLabel = new Label(BEST_RUN, labelStyle);
        bestRunLabel.setSize(80, 80);
        bestRunLabel.setAlignment(Align.center);
        bestRunLabel.setPosition(1060, 200);

        Label thisScoreLabel = new Label(score.toString(), labelStyle);
        thisScoreLabel.setSize(80, 80);
        thisScoreLabel.setAlignment(Align.center);
        thisScoreLabel.setPosition(760, 150);

        Label bestScoreLabel = new Label(bestScore.toString(), labelStyle);
        bestScoreLabel.setSize(80, 80);
        bestScoreLabel.setAlignment(Align.center);
        bestScoreLabel.setPosition(1060, 150);

        stage.addActor(thisRunLabel);
        stage.addActor(bestRunLabel);
        stage.addActor(thisScoreLabel);
        stage.addActor(bestScoreLabel);
    }
    private BitmapFont getRedirectAndScoreButtonsStyle(){
        parameter.size = 20;
        parameter.borderWidth = 2;
        parameter.color = redColor;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }

    private BitmapFont getYouLostStyle(){
        parameter.size = 50;
        parameter.borderWidth = 3;
        parameter.color = redColor;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }

    private BitmapFont getSkullsStyle(){
        parameter.size = 30;
        parameter.borderWidth = 3;
        parameter.color = redColor;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }
}
