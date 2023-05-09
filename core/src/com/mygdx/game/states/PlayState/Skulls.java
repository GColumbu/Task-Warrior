package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Skulls {
    private final Texture skullTexture;
    private Color magicColor;
    private Label.LabelStyle labelStyle;
    public Skulls(String skullTexture){
        this.skullTexture = new Texture(skullTexture);
        this.labelStyle = new Label.LabelStyle();
        this.magicColor = new Color(100/255f, 240/255f, 195/255f, 1);
        this.labelStyle.font = getFontStyle();
    }
    public void draw(SpriteBatch batch, OrthographicCamera camera,  Integer skulls){
        float x = getSkullsPosition(camera).x;
        float y = getSkullsPosition(camera).y;
        Label label = new Label(skulls.toString(), labelStyle);
        label.setSize(100, 100);
        label.setPosition(x + 120, y);
        batch.draw(skullTexture, x, y);
        label.draw(batch, 1);
    }

    // get position
    private Vector2 getSkullsPosition(OrthographicCamera camera){
        return new Vector2(camera.position.x + (camera.viewportWidth/2 * camera.zoom) - 250, camera.position.y + (camera.viewportHeight/2 * camera.zoom) - 130);
    }

    // get font style
    private BitmapFont getFontStyle(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/pixel_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.borderWidth = 2;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.shadowColor = magicColor;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
