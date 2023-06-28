package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.states.TemporaryAccountDetails;

public class TasksState implements Screen {
    // screen utils
    private TaskWarrior game;
    private final Stage stage;
    private final Viewport viewport;
    private TemporaryAccountDetails accountDetails;

    // Screen Componenets
    private CreateTaskSection createTaskSection;
    private ViewTasksSection viewTasksSection;


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
    }

    private void draw() {
        createTaskSection.draw();
        viewTasksSection.draw();
    }
}
