package com.mygdx.game.states.PlayState;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.enemies.Enemy;
import com.mygdx.game.enemies.Minion;
import com.mygdx.game.players.garen.Garen;
import com.mygdx.game.players.PlayerChampion;
import com.mygdx.game.states.PlayState.UI.UserInterface;
import jdk.internal.org.jline.utils.ShutdownHooks;

import java.util.ArrayList;
import java.util.List;

public class PlayState implements Screen {
    private static int NR_OF_MINIONS = 2;

    // game utils
    private TaskWarrior game;
    private final Texture background;
    protected Viewport viewport;
    protected Viewport minimapReference;
    private  OrthographicCamera camera;

    // game characters
    private List<Enemy> minions;
    private final PlayerChampion target;
    private final UserInterface userInterface;
    private final Minimap minimap;
    ShapeRenderer borderShapeRenderer;
    public PlayState(TaskWarrior game){
        this.game = game;
        background = new Texture("background.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT);
        minions = new ArrayList<>();
        for(int i=0; i<NR_OF_MINIONS; i++){
            minions.add(new Minion(i * 100, i * 300));
        }
        target = new Garen(TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT/2);
        borderShapeRenderer = new ShapeRenderer();
        userInterface = new UserInterface("assets/UI bar.png", target, camera);
        viewport = new ExtendViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT, camera);
        minimapReference = new FitViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT);
        minimap = new Minimap(new Texture("minimap.png"));
        MyInputProcessor myInputProcessor = new MyInputProcessor(camera, target);
        Gdx.input.setInputProcessor(myInputProcessor);
    }

    // override methods

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        minimapReference.update(width, height, true);
        minimap.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //dispose minions
        for(int i=0; i<NR_OF_MINIONS; i++){
            minions.get(i).getSprite().getTexture().dispose();
        }
        //dispose player
        target.getSprite().getTexture().dispose();
    }


    // methods utils

    private void update(float deltaTime) {
        target.update(deltaTime);
        for(int i=0; i<NR_OF_MINIONS; i++){
            minions.get(i).update(target, deltaTime);
            if(minions.get(i).getHealth() <= 0){
                minions.remove(i);
                NR_OF_MINIONS--;
                i--;
            }
        }
        //showBorders();
    }

    private void render() {
        // game objects
        drawGameViewport();

        // minimap
        minimap.draw(game.batch, target, minimapReference);
    }

    private void drawGameViewport(){
        viewport.apply();
        viewport.getCamera().viewportWidth = Gdx.graphics.getWidth();
        viewport.getCamera().viewportHeight = Gdx.graphics.getHeight();
        updateCamera(target);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0);
        game.batch.draw(target.getSprite(), target.getRelativePosition().x, target.getRelativePosition().y,
                target.getSprite().getRegionWidth() / 2,
                target.getSprite().getRegionHeight() / 2, target.getSprite().getRegionWidth(),
                target.getSprite().getRegionHeight(), 1, 1,
                target.getHeading());
        for(int i=0; i<NR_OF_MINIONS; i++) {
            game.batch.draw(minions.get(i).getSprite(), minions.get(i).getRelativePosition().x, minions.get(i).getRelativePosition().y,
                    minions.get(i).getSprite().getRegionWidth()/ 2,
                    minions.get(i).getSprite().getRegionHeight() / 2, minions.get(i).getSprite().getRegionWidth(),
                    minions.get(i).getSprite().getRegionHeight(), 1, 1,
                    minions.get(i).getHeading());
        }
        userInterface.draw(game.batch, camera, target);
        borderShapeRenderer.end();
        game.batch.end();
    }

    //update camera position based on background limits
    private void updateCamera(PlayerChampion target){
        if(target.getPosition().x - camera.viewportWidth/2 * camera.zoom > 0 && target.getPosition().x + camera.viewportWidth/2 * camera.zoom < TaskWarrior.WIDTH){
            followPlayerX(target);
        }
        if(target.getPosition().y - camera.viewportHeight/2 * camera.zoom > 0 && target.getPosition().y + camera.viewportHeight/2 * camera.zoom< TaskWarrior.HEIGHT){
            followPlayerY(target);
        }
        camera.update();
    }

    //update camera position based on the player and axis
    private void followPlayerX(PlayerChampion target){
        camera.position.x = target.getPosition().x;
    }
    private void followPlayerY(PlayerChampion target){
        camera.position.y = target.getPosition().y;
    }

    private void showBorders(){
        borderShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        borderShapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        showBordersForChampion(borderShapeRenderer, target);

        // show borders for minions
        for(int i=0; i<NR_OF_MINIONS; i++){
            Rectangle minionRect = minions.get(i).getEnemyRectangle();
            borderShapeRenderer.rect(minionRect.getX(), minionRect.getY(), minionRect.getWidth(), minionRect.getHeight());
        }
    }

    private void showBordersForChampion(ShapeRenderer shapeRenderer, PlayerChampion target){
        // border for champion
        shapeRenderer.rect(target.getPlayerRectangle().getX(), target.getPlayerRectangle().getY(), target.getPlayerRectangle().getWidth(), target.getPlayerRectangle().getHeight());

        // border for champion ability
        if(target.getState() == PlayerChampion.State.E && target.isEAttackTiming(true)){
            drawShape(target.getEAttackRange(), shapeRenderer);
        }
        if(target.getState() == PlayerChampion.State.Q && target.isQAttackTiming(true)){
            drawShape(target.getQAttackRange(), shapeRenderer);
        }
        if(target.getState() == PlayerChampion.State.W && target.isWAttackTiming(true)){
            drawShape(target.getWAttackRange(), shapeRenderer);
        }
    }

    private void drawShape(Shape2D shape, ShapeRenderer shapeRenderer){
        if(shape instanceof Rectangle){
            Rectangle rectangle = (Rectangle)shape;
            shapeRenderer.rect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        } else if(shape instanceof Polygon){
            Polygon polygon = (Polygon)shape;
            shapeRenderer.polygon(polygon.getVertices());
        } else if (shape instanceof Circle){
            Circle circle = (Circle)shape;
            shapeRenderer.circle(circle.x, circle.y, circle.radius);
        }
    }
}
