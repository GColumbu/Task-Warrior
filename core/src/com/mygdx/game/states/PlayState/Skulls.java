package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class Skulls {
    private final Texture skullTexture;
    private Color magicColor;
    private Label.LabelStyle labelStyle;
    private final FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    public Skulls(String skullTexture){
        this.skullTexture = new Texture(skullTexture);
        this.labelStyle = new Label.LabelStyle();
        this.magicColor = new Color(100/255f, 240/255f, 195/255f, 1);
        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/pixel_font.ttf"));
        this.parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    }
    public void draw(SpriteBatch batch, OrthographicCamera camera,  Integer skulls){
        labelStyle.font = getFontStyle(camera.zoom);
        Vector2 position = getSkullsPosition(camera.position, camera.zoom, camera.viewportWidth, camera.viewportHeight);

        // draw skull texture
        batch.draw(skullTexture, position.x, position.y, skullTexture.getWidth() * camera.zoom, skullTexture.getHeight() * camera.zoom);

        // draw no. of skulls
        Label label = new Label(skulls.toString(), labelStyle);
        label.setSize(100 * camera.zoom, 100 * camera.zoom);
        label.setAlignment(Align.center);
        label.setPosition(position.x + 80 * camera.zoom, position.y - 15 * camera.zoom);
        label.draw(batch, 1);
    }

    // get position
    private Vector2 getSkullsPosition(Vector3 cameraPosition, float cameraZoom, float viewportWidth, float viewportHeight){
        return new Vector2(cameraPosition.x + (viewportWidth/2 - 230) * cameraZoom, cameraPosition.y + (viewportHeight/2 - 90)  * cameraZoom);
    }

    // get font style
    private BitmapFont getFontStyle(float cameraZoom){
        parameter.size = (int)(30 * cameraZoom);
        parameter.borderWidth = 2 * cameraZoom;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = (int)(3 * cameraZoom);
        parameter.shadowOffsetY = (int)(3 * cameraZoom);
        parameter.shadowColor = magicColor;
        return generator.generateFont(parameter);
    }

    public void dispose(){
        skullTexture.dispose();
        generator.dispose();
    }
}
