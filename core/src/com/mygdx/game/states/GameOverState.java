package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;

public class GameOverState implements Screen {

    private final TaskWarrior game;
    private final Texture background;
    private Viewport viewport;
    private Music gameOverMusic;

    public GameOverState(TaskWarrior game) {
        this.game = game;
        this.game.batch = new SpriteBatch();
        this.background = new Texture("assets/game over screen/background_sword.png");
        this.viewport = new ScreenViewport();
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/game_over_music.mp3"));
        gameOverMusic.setLooping(true);
        gameOverMusic.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
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
        background.dispose();
    }

    private void render(){
        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.batch.end();
    }
}
