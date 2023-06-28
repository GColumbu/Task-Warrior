package com.mygdx.game.states.TasksState;

public class Task {
    private String title;

    private String difficulty;

    public Task(String title, String difficulty){
        this.title = title;
        this.difficulty = difficulty;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
