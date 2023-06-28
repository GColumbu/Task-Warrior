package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class CreateTaskSection extends Section {
    // CONSTANTS
    private static final String CREATE_TASK = "CREATE TASK";
    private static final String TASK_NAME = "Task Name: ";
    private static final String DIFFICULTY = "Difficulty: ";
    private static final String SUBMIT = "SUBMIT";

    private List<Task> tasks;

    private DifficultyButtons difficultyButtons;
    private TextField taskName;
    private Label taskNameLabel;
    private Label difficultyLabel;
    private Image container;
    private Button submitButton;

    protected CreateTaskSection(Stage stage, List<Task> tasks){
        super();
        this.stage = stage;
        this.tasks = tasks;

        // configure
        configureTitle();
        configureContainer();
        configureSubTitle();
        configureTextFields();
        configureSubmitButton();

        difficultyButtons = new DifficultyButtons(stage);
    }

    protected void update(){
        disableTextFieldWhenMaxTasksAchieved();
    }

    protected void draw(){
        difficultyButtons.draw();
    }

    // update methods
    private void disableTextFieldWhenMaxTasksAchieved(){
        if(tasks.size() == 5){
            taskName.setDisabled(true);
            difficultyButtons.easyButton.setDisabled(true);
            difficultyButtons.mediumButton.setDisabled(true);
            difficultyButtons.hardButton.setDisabled(true);
        }
    }

    // configure methods
    private void configureTitle(){
        labelStyle.font = getTitleStyle();

        Label titleLabel = new Label(CREATE_TASK, labelStyle);
        titleLabel.setSize(100, 100);
        titleLabel.setAlignment(Align.center);
        titleLabel.setPosition(340, Gdx.graphics.getHeight() - 210);
        stage.addActor(titleLabel);
    }

    private void configureContainer(){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(magicColor);
        pixmap.fill();
        container = new Image(new Texture(pixmap));
        pixmap.dispose();
        container.setSize(850, 200);
        container.setPosition(90, Gdx.graphics.getHeight() - 450);
        stage.addActor(container);
    }

    private void configureSubmitButton(){
        // configure button
        Pixmap pixmapUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapUp.setColor(magicColor);
        pixmapUp.fill();
        TextureRegionDrawable upButtonTexture = new TextureRegionDrawable(new Texture(pixmapUp));
        pixmapUp.dispose();

        Pixmap pixmapChecked = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapChecked.setColor(magicColorChecked);
        pixmapChecked.fill();
        TextureRegionDrawable checkedButtonTexture = new TextureRegionDrawable(new Texture(pixmapChecked));
        pixmapChecked.dispose();

        Button.ButtonStyle submitButtonStyle = new Button.ButtonStyle();
        submitButtonStyle.up = upButtonTexture;
        submitButtonStyle.down = checkedButtonTexture;

        submitButton = new Button(submitButtonStyle);
        submitButton.setPosition(430, Gdx.graphics.getHeight() - 520);
        submitButton.setSize(200, 50);
        submitButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (tasks.size() < 5) {
                    String difficulty;
                    if (difficultyButtons.easyButton.isChecked()) {
                        difficulty = "easy";
                    } else if (difficultyButtons.mediumButton.isChecked()) {
                        difficulty = "medium";
                    } else if (difficultyButtons.hardButton.isChecked()) {
                        difficulty = "hard";
                    } else {
                        difficulty = "easy";
                    }
                    Task addedTask = new Task(taskName.getMessageText(), difficulty);
                    tasks.add(addedTask);
                    taskName.setText("");
                } else {
                    taskName.setText("");
                }
            }
        } );

        // configure button label
        labelStyle.font = getSubTitleStyle();
        Label submitLabel = new Label(SUBMIT, labelStyle);

        submitButton.add(submitLabel);
        stage.addActor(submitButton);
    }

    private void configureSubTitle(){
        labelStyle.font = getSubTitleStyle();

        taskNameLabel = new Label(TASK_NAME, labelStyle);
        taskNameLabel.setSize(100, 100);
        taskNameLabel.setAlignment(Align.center);
        taskNameLabel.setPosition(175, Gdx.graphics.getHeight() - 365);

        difficultyLabel = new Label(DIFFICULTY, labelStyle);
        difficultyLabel.setSize(100, 100);
        difficultyLabel.setAlignment(Align.center);
        difficultyLabel.setPosition(175, Gdx.graphics.getHeight() - 450);

        stage.addActor(taskNameLabel);
        stage.addActor(difficultyLabel);
    }

    private void configureTextFields(){
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();

        // create background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        textFieldStyle.background = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();

        // create cursor
        pixmap = new Pixmap(1, Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fillRectangle(0, 0, 1, Gdx.graphics.getHeight());
        textFieldStyle.cursor = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();

        // set style
        textFieldStyle.font = getTextFieldStyle();
        textFieldStyle.fontColor = Color.WHITE;
        taskName = new TextField("", textFieldStyle);
        taskName.setSize(550,70);
        taskName.setPosition(350, Gdx.graphics.getHeight() - 350);
        taskName.setAlignment(Align.center);

        stage.addActor(taskName);
    }

    // style methods
    private BitmapFont getSubTitleStyle(){
        parameter.size = 25;
        parameter.borderWidth = 1;
        parameter.color = Color.BLACK;
        return generator.generateFont(parameter);
    }

    private BitmapFont getTextFieldStyle(){
        parameter.size = 20;
        parameter.borderWidth = 2;
        parameter.color = magicColor;
        return generator.generateFont(parameter);
    }
}
