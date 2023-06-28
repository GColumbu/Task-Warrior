package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.states.PlayState.PlayState;

public class DifficultyButtons {
    // STAGE
    private Stage stage;

    // COLORS

    private final Color easyColor = Color.GREEN;
    private final Color mediumColor = Color.YELLOW;
    private final Color hardColor = Color.RED;

    // BUTTONS
    protected ButtonGroup<CheckBox> buttonGroup;
    private FileHandle checkboxIcon;
        // Easy Button
    protected CheckBox easyButton;
    private CheckBox.CheckBoxStyle easyButtonStyle;

        // Medium Button
        protected CheckBox mediumButton;
    private CheckBox.CheckBoxStyle mediumButtonStyle;

        // Hard Button
        protected CheckBox hardButton;
    private CheckBox.CheckBoxStyle hardButtonStyle;


    // GdxFreeType
    private final FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    protected DifficultyButtons(Stage stage){
        this.stage = stage;

        this.checkboxIcon = Gdx.files.internal("assets/checkbox.png");

        // GdxFreeType
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/pixel_font.ttf"));
        this.parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // configure buttons
        configureEasyButton();
        configureMediumButton();
        configureHardButton();

        buttonGroup = new ButtonGroup<>();
        buttonGroup.add(easyButton, mediumButton, hardButton);
        buttonGroup.setMinCheckCount(0);
        buttonGroup.setMaxCheckCount(1);

        stage.addActor(easyButton);
        stage.addActor(mediumButton);
        stage.addActor(hardButton);
    }

    // configure methods
    private void configureEasyButton(){
        easyButtonStyle = new CheckBox.CheckBoxStyle();
        easyButtonStyle.checkboxOn = getButtonTexture(easyColor, true);
        easyButtonStyle.checkboxOff  = getButtonTexture(easyColor, false);
        easyButtonStyle.font = getWritingStyle();

        easyButton = new CheckBox(" Easy", easyButtonStyle);
        easyButton.setChecked(false);
        easyButton.setPosition(350, Gdx.graphics.getHeight() - 430);
    }

    private void configureMediumButton(){
        mediumButtonStyle = new CheckBox.CheckBoxStyle();
        mediumButtonStyle.checkboxOn = getButtonTexture(mediumColor, true);
        mediumButtonStyle.checkboxOff  = getButtonTexture(mediumColor, false);
        mediumButtonStyle.font = getWritingStyle();

        mediumButton = new CheckBox(" Medium", mediumButtonStyle);
        mediumButton.setChecked(false);
        mediumButton.setPosition(550, Gdx.graphics.getHeight() - 430);
    }

    private void configureHardButton(){
        hardButtonStyle = new CheckBox.CheckBoxStyle();
        hardButtonStyle.checkboxOn = getButtonTexture(hardColor, true);
        hardButtonStyle.checkboxOff  = getButtonTexture(hardColor, false);
        hardButtonStyle.font = getWritingStyle();

        hardButton = new CheckBox(" Hard", hardButtonStyle);
        hardButton.setChecked(false);
        hardButton.setPosition(750, Gdx.graphics.getHeight() - 430);
    }

    // methods for styling
    private TextureRegionDrawable getButtonTexture(Color color, boolean isChecked){
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        if(isChecked){
            pixmap.drawPixmap(new Pixmap(checkboxIcon), 0, 0);
        }
        TextureRegionDrawable buttonTexture = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();
        return buttonTexture;
    }

    private BitmapFont getWritingStyle(){
        parameter.size = 20;
        parameter.borderWidth = 2;
        parameter.color = Color.WHITE;
        return generator.generateFont(parameter);
    }

    public void draw(){
        stage.draw();
    }
}
