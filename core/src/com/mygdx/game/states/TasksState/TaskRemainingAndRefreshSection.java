package com.mygdx.game.states.TasksState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.enemies.Minion;
import com.mygdx.game.enemies.Runner;
import com.mygdx.game.enemies.Troll;
import com.mygdx.game.states.TemporaryAccountDetails;

import java.util.HashMap;
import java.util.List;

public class TaskRemainingAndRefreshSection extends Section{
    private static final String TASKS_REMAINING = "REMAINING TASKS FOR TODAY:";
    private static final String COLLECT = "COLLECT";

    private TemporaryAccountDetails accountDetails;
    private Button refreshButton;

    private BitmapFont subTitleFont;
    private Label coinsLabel;

    private Image coinsImage;
    private final HashMap<String, Integer> tasksReward = new HashMap<String, Integer>() {{
        put("easy", 100);
        put("medium", 150);
        put("hard", 200);
    }};
    protected TaskRemainingAndRefreshSection(Stage stage, TemporaryAccountDetails accountDetails){
        this.stage = stage;
        this.accountDetails = accountDetails;
        this.subTitleFont = getSubTitleStyle();
        configureTasksRemainingLabel();
        configureReloadButton();
        configureCoins();
    }

    private void configureTasksRemainingLabel() {
        labelStyle.font = getRemainingTasksLabelStyle();

        Label titleLabel = new Label(TASKS_REMAINING, labelStyle);
        titleLabel.setSize(100, 100);
        titleLabel.setAlignment(Align.left);
        titleLabel.setPosition(120, 300);
        stage.addActor(titleLabel);
    }

    private void configureReloadButton() {
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

        refreshButton = new Button(submitButtonStyle);
        refreshButton.setPosition(530, 230);
        refreshButton.setSize(200, 50);
        refreshButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for(int i=0; i<accountDetails.getTasks().size(); i++){
                    if(accountDetails.getTasks().get(i).getCheckbox().isChecked()){
                        // remove visual elements
                        accountDetails.getTasks().get(i).getContainer().remove();
                        accountDetails.getTasks().get(i).getCheckbox().remove();
                        accountDetails.getTasks().get(i).getTask().getTitle().remove();
                        accountDetails.setCoins(accountDetails.getCoins() + tasksReward.get(accountDetails.getTasks().get(i).getTask().getDifficulty()));
                        accountDetails.getTasks().remove(i);
                    }
                }
            }
        } );

        // configure button label
        labelStyle.font = getSubTitleStyle();
        Label refreshLabel = new Label(COLLECT, labelStyle);

        refreshButton.add(refreshLabel);
        stage.addActor(refreshButton);
    }

    private BitmapFont getRemainingTasksLabelStyle(){
        parameter.size = 20;
        parameter.borderWidth = 2;
        parameter.color = magicColor;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;
        return generator.generateFont(parameter);
    }

    protected void draw(){
        for(int i=0; i < 5;i++){
            if(i < accountDetails.getTasks().size())
                configureTaskMiniContainer(i, true);
            else
                configureTaskMiniContainer(i, false);
        }
        drawCoinsNumber();
        stage.draw();
        coinsLabel.remove();
    }

    private void configureCoins(){
        coinsImage = new Image(new Texture("assets/coins.png"));
        coinsImage.setPosition(350, 220);
        stage.addActor(coinsImage);
    }

    private void drawCoinsNumber(){
        labelStyle.font = subTitleFont;
        coinsLabel = new Label(accountDetails.getCoins().toString(), labelStyle);
        coinsLabel.setSize(100, 100);
        coinsLabel.setAlignment(Align.left);
        coinsLabel.setPosition(425, 205);
        stage.addActor(coinsLabel);
    }

    private void configureTaskMiniContainer(int i, boolean isTaskCreated){
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        if(isTaskCreated)
            pixmap.setColor(magicColor);
        else
            pixmap.setColor(Color.GRAY);
        pixmap.fill();
        Image taskContainer = new Image(new Texture(pixmap));
        pixmap.dispose();
        taskContainer.setSize(20, 50);
        taskContainer.setPosition(660 + 55*i, 325);
        stage.addActor(taskContainer);
    }
}
