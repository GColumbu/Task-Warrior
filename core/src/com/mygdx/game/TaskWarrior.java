package com.mygdx.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.states.MenuState;

public class TaskWarrior extends Game {
	// window constants
	public static final int WIDTH = 5333;
	public static final int HEIGHT = 3467;
	public static final String TITLE = "Task Warrior";

	//private GameStateManager gsm;
	public SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new MenuState(this));
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
