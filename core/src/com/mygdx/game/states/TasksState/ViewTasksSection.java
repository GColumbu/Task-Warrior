package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class ViewTasksSection extends Section{
    private static final String CURRENT_TASKS = "CURRENT TASKS";
    private List<Task> tasks;

    protected ViewTasksSection(Stage stage, List<Task> tasks){
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

    private void configureTaskContainer(String difficulty, int index){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        if(difficulty.equals("easy")){
            pixmap.setColor(Color.GREEN);
        } else if(difficulty.equals("medium")){
            pixmap.setColor(Color.YELLOW);
        } else if(difficulty.equals("hard")){
            pixmap.setColor(Color.RED);
        }
        pixmap.fill();
        Image taskContainer = new Image(new Texture(pixmap));
        pixmap.dispose();
        taskContainer.setSize(800, 150);
        taskContainer.setPosition(1000, Gdx.graphics.getHeight() - 380 - 165*index);
        stage.addActor(taskContainer);
    }

    private void configureCheckBox(int index){
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOn = getButtonTexture(Color.WHITE, true);
        checkBoxStyle.checkboxOff  = getButtonTexture(Color.WHITE, false);
        checkBoxStyle.font = getWritingStyle();

        final CheckBox checkBox = new CheckBox("", checkBoxStyle);
        checkBox.setChecked(false);
        checkBox.setSize(50, 50);
        checkBox.setPosition(1020, Gdx.graphics.getHeight() - 330 - 165*index);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean isChecked = checkBox.isChecked();
                // Handle the checkbox state change
            }
        });
        stage.addActor(checkBox);
    }

    private TextureRegionDrawable getButtonTexture(Color color, boolean isChecked){
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        if(isChecked){
            pixmap.drawPixmap(new Pixmap(Gdx.files.internal("assets/checkbox.png")), 0, 0);
        }
        TextureRegionDrawable buttonTexture = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        return buttonTexture;
    }

    private BitmapFont getWritingStyle(){
        parameter.size = 50;
        parameter.borderWidth = 2;
        parameter.color = Color.WHITE;
        return generator.generateFont(parameter);
    }


    protected void draw(){
        for(int i=0; i< tasks.size(); i++){
            configureTaskContainer(tasks.get(i).getDifficulty(), i);
            configureCheckBox(i);
        }
        stage.draw();
    }

}
