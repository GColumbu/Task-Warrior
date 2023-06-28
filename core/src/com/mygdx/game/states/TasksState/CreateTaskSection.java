package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class CreateTaskSection extends Section {
    // CONSTANTS
    private static final String CREATE_TASK = "CREATE TASK";
    private static final String TASK_NAME = "Task Name: ";
    private static final String DIFFICULTY = "Difficulty: ";
    private static final String SUBMIT = "SUBMIT";

    private DifficultyButtons difficultyButtons;
    private TextField taskName;
    private Label taskNameLabel;
    private Label difficultyLabel;
    private Image container;
    private Button submitButton;

    protected CreateTaskSection(Stage stage){
        this.stage = stage;

        // configure
        configureTitle();
        configureContainer();
        configureSubTitle();
        configureTextFields();
        configureSubmitButton();

        difficultyButtons = new DifficultyButtons(stage);
    }

    protected void draw(){
        difficultyButtons.draw();
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
        container.setSize(Gdx.graphics.getWidth() / 2.0f - 150, 200);
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
        submitButton.setPosition(Gdx.graphics.getWidth() / 4.0f - 100, Gdx.graphics.getHeight() - 520);
        submitButton.setSize(200, 50);

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
        taskName.sizeBy(Gdx.graphics.getWidth() / 2.0f - 600,15);
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
    private BitmapFont getTitleStyle(){
        parameter.size = 50;
        parameter.borderWidth = 3;
        parameter.color = magicColor;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }
    private BitmapFont getTextFieldStyle(){
        parameter.size = 20;
        parameter.borderWidth = 2;
        parameter.color = magicColor;
        return generator.generateFont(parameter);
    }
}
