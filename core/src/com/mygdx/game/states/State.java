package com.mygdx.game.states;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public abstract class State {
    protected OrthographicCamera camera;
    protected Vector3 mouse;
    protected GameStateManager gsm;

    //constructor
    protected State(GameStateManager gsm){
        this.gsm = gsm;
        camera = new OrthographicCamera();
        mouse = new Vector3();
    }

    //handles input from the user
    protected abstract void handleInput();

    //game loop function update
    protected abstract void update(float deltaTime);

    //game loop function render
    protected abstract void render(SpriteBatch batch);

    //dispose resources
    protected abstract void dispose();
}
