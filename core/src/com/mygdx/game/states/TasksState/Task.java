package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Task {
    private Label title;
    private String difficulty;

    public Task(Label title, String difficulty){
        this.title = title;
        this.difficulty = difficulty;
    }

    public Label getTitle() {
        return title;
    }

    public void setTitle(Label title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
