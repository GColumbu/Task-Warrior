package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public abstract class Section {
    // STAGE
    protected Stage stage;

    // GdxFreeType
    protected final Label.LabelStyle labelStyle;
    protected final FreeTypeFontGenerator generator;
    protected FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    // sound
    protected Sound selectSound;

    // Color
    protected final Color magicColor;
    protected final Color magicColorChecked;

    Section(){
        this.magicColor = new Color(100/255f, 240/255f, 195/255f, 1);
        this.magicColorChecked = new Color(42/255f, 167/255f, 216/255f, 1);
        this.selectSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/select.mp3"));

        // GdxFreeType
        this.labelStyle = new Label.LabelStyle();
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/pixel_font.ttf"));
        this.parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    }

    protected BitmapFont getTitleStyle(){
        parameter.size = 50;
        parameter.borderWidth = 3;
        parameter.color = magicColor;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }
}
