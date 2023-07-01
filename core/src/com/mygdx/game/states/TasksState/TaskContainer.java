package com.mygdx.game.states.TasksState;


import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


public class TaskContainer {
    private Task task;
    private CheckBox checkbox;

    private Image container;

    public TaskContainer(Task task, CheckBox checkbox, Image container) {
        this.task = task;
        this.checkbox = checkbox;
        this.container = container;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(CheckBox checkbox) {
        this.checkbox = checkbox;
    }

    public Image getContainer() {
        return container;
    }

    public void setContainer(Image container) {
        this.container = container;
    }
}
