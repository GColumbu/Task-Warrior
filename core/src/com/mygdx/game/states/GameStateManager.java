package com.mygdx.game.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

public class GameStateManager {
    //stack of states
    private Stack<State> states;

    //constructor
    public GameStateManager(){
        states = new Stack<>();
    }

    //add an element to the stack
    public void push(State state){
        states.push(state);
    }

    //remove an element from the stack
    public void pop(){
        states.pop();
    }

    //update the peak of the stack
    public void update(float deltaTime){
        states.peek().update(deltaTime);
    }

    //render the peak of the stack
    public void render(SpriteBatch batch){
        states.peek().render(batch);
    }
}
