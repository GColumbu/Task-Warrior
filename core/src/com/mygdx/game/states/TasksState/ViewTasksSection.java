package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class ViewTasksSection extends Section{
    private static final String CURRENT_TASKS = "CURRENT TASKS";
    private List<TaskContainer> tasks;

    protected ViewTasksSection(Stage stage, List<TaskContainer> tasks){
        this.stage = stage;
        this.tasks = tasks;
        configureTitle();
    }

    private void configureTitle(){
        labelStyle.font = getTitleStyle();

        Label titleLabel = new Label(CURRENT_TASKS, labelStyle);
        titleLabel.setSize(100, 100);
        titleLabel.setAlignment(Align.center);
        titleLabel.setPosition(1300, Gdx.graphics.getHeight() - 210);
        stage.addActor(titleLabel);
    }

    private void configureTaskContainer(int index){
        Image taskContainer = tasks.get(index).getContainer();
        taskContainer.setPosition(1000, 670 - 165*index);
        stage.addActor(taskContainer);
    }

    private void configureTaskName(int index){;
        Label taskName = tasks.get(index).getTask().getTitle();
        taskName.setSize(100, 100);
        taskName.setAlignment(Align.left);
        taskName.setPosition(1100, 690 - 165*index);
        stage.addActor(taskName);
    }

    private void configureCheckBox(int index){
        CheckBox checkBox = tasks.get(index).getCheckbox();
        checkBox.setPosition(1020, 680 - 165*index);
        stage.addActor(checkBox);
    }

    protected void draw(){
        for(int i=0; i < tasks.size(); i++){
            configureTaskContainer(i);
            configureCheckBox(i);
            configureTaskName(i);
        }
        stage.draw();
    }

}
