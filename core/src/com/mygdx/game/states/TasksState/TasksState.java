package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.states.MenuState;
import com.mygdx.game.states.TemporaryAccountDetails;

public class TasksState implements Screen {
    private static final String BACK = "MAIN MENU";

    // screen utils
    private TaskWarrior game;
    private final Stage stage;
    private final Viewport viewport;
    private TemporaryAccountDetails accountDetails;

    // Screen Componenets
    private CreateTaskSection createTaskSection;
    private ViewTasksSection viewTasksSection;
    private TaskRemainingAndRefreshSection taskRemainingAndRefreshSection;
    private Button backToMainMenu;


    public TasksState(TaskWarrior game, TemporaryAccountDetails accountDetails){
        // screen utils
        this.game = game;
        this.game.batch = new SpriteBatch();
        this.stage = new Stage();
        this.viewport = new ScreenViewport();
        this.accountDetails = accountDetails;

        // screen components
        createTaskSection = new CreateTaskSection(stage, accountDetails.getTasks());
        viewTasksSection = new ViewTasksSection(stage, accountDetails.getTasks());
        taskRemainingAndRefreshSection = new TaskRemainingAndRefreshSection(stage, accountDetails);

        configureBackToMainMenuButton();


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

    }

    private void update(float deltaTime){
        viewport.getCamera().update();
        stage.getBatch().setProjectionMatrix(viewport.getCamera().combined);
        // make screen black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(deltaTime);
        createTaskSection.update();
        addHoverLogicForButton();
    }

    private void draw() {
        createTaskSection.draw();
        viewTasksSection.draw();
        taskRemainingAndRefreshSection.draw();
    }

    private void configureBackToMainMenuButton(){
        // button
        TextureRegionDrawable buttonTexture = new TextureRegionDrawable(new TextureRegion(new Texture("assets/main menu screen/main_button.png")));
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = buttonTexture;
        backToMainMenu = new Button(buttonStyle);
        backToMainMenu.setPosition(30, Gdx.graphics.getHeight() - 85);
        backToMainMenu.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuState(game, accountDetails));
            }
        } );

        // label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = getRedirectButtonsStyle();
        Label championLabel = new Label(BACK, labelStyle);
        backToMainMenu.add(championLabel);
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

    private BitmapFont getRedirectButtonsStyle(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/pixel_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        parameter.borderWidth = 3;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }
}
