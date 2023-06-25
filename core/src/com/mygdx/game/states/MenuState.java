package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.states.PlayState.PlayState;

public class MenuState implements Screen {
    private TaskWarrior game;
    private Texture img;
    private Integer bestScore;
    public MenuState(TaskWarrior game, Integer bestScore) {
        this.game = game;
        img = new Texture("home_background.jpg");
        this.bestScore = bestScore;
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
        img.dispose();
    }

    private void handleInput() {
        if(Gdx.input.justTouched()){
            game.setScreen( new PlayState(game, bestScore));
        }
    }

    private void update(float deltaTime){
        handleInput();
    }

    private void render(){
        game.batch.begin();
        game.batch.draw(img, 0, 0);
        game.batch.end();
    }
}
