package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuState extends State{
    private Texture img;
    public MenuState(GameStateManager gsm) {
        super(gsm);
        img = new Texture("home_background.jpg");
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            gsm.push(new PlayState(gsm));
        }
    }

    @Override
    protected void update(float deltaTime) {
        handleInput();
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    protected void dispose() {
        img.dispose();
    }
}
