package com.mygdx.game.states;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class State{
    protected OrthographicCamera camera;
    protected Vector3 mouse;
    protected GameStateManager gsm;

    protected Viewport viewport;

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
    public abstract void dispose();

}
