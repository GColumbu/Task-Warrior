package com.mygdx.game.states.PlayState;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.enemies.Enemy;
import com.mygdx.game.enemies.Minion;
import com.mygdx.game.players.garen.Garen;
import com.mygdx.game.players.PlayerChampion;
import com.mygdx.game.states.PlayState.UI.UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayState implements Screen {

    private static final int ENEMY_SPAWN_FREQUENCY = 2;

    // game utils
    private final TaskWarrior game;
    private final Texture background;
    protected Viewport viewport;
    protected Viewport minimapReference;
    private final OrthographicCamera camera;
    private float spawnTimer;
    private int nrOfMinions = 7;

    // game characters
    private final List<Enemy> minions;
    private final PlayerChampion target;
    private final UserInterface userInterface;
    private final Minimap minimap;

    // shape renderer for debug purposes
    ShapeRenderer shapeRenderer;

    // constructor
    public PlayState(TaskWarrior game){
        this.game = game;
        spawnTimer = 0;
        background = new Texture("background.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT);
        minions = new ArrayList<>();
        for(int i = 0; i< nrOfMinions; i++){
            minions.add(new Minion(getRandomValue("x"), getRandomValue("y")));
        }
        target = new Garen(TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT/2);
        shapeRenderer = new ShapeRenderer();
        userInterface = new UserInterface("assets/UI bar.png", target, camera);
        viewport = new ExtendViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT, camera);
        minimapReference = new FitViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT);
        minimap = new Minimap(new Texture("minimap.png"));
        MyInputProcessor myInputProcessor = new MyInputProcessor(camera, target);
        Gdx.input.setInputProcessor(myInputProcessor);
    }

    // OVERRIDE STATE METHODS

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
        //dispose background
        background.dispose();
        //dispose UI
        userInterface.dispose();
        //dispose minimap
        minimap.dispose();
        //dispose minions
        for(int i = 0; i< nrOfMinions; i++){
            minions.get(i).getSprite().getTexture().dispose();
        }
        //dispose player
        target.getSprite().getTexture().dispose();
    }


    // methods utils

    private void update(float deltaTime) {
        target.update(deltaTime);
        for(int i = 0; i< nrOfMinions; i++){
            minions.get(i).update(target, deltaTime);
            if(minions.get(i).isDyingAnimationFinished()){
                minions.remove(i);
                nrOfMinions--;
                i--;
            }
        }
        for(int i = 0; i< nrOfMinions; i++){
            minions.get(i).move(target, minions, deltaTime);
        }
        showBorders();
        addMinion(target, deltaTime);
    }

    private void render() {
        // game objects
        drawGameViewport();

        // minimap
        minimap.draw(game.batch, minimapReference, target, minions);
    }

    // draw method for game objects
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
        for(int i = 0; i< nrOfMinions; i++) {
            game.batch.draw(minions.get(i).getSprite(), minions.get(i).getRelativePosition().x, minions.get(i).getRelativePosition().y,
                    minions.get(i).getSprite().getRegionWidth()/ 2,
                    minions.get(i).getSprite().getRegionHeight() / 2, minions.get(i).getSprite().getRegionWidth(),
                    minions.get(i).getSprite().getRegionHeight(), 1, 1,
                    minions.get(i).getHeading());
        }
        userInterface.draw(game.batch, camera, target);
        shapeRenderer.end();
        game.batch.end();
    }

    // ENEMY SPAWNING METHODS
    private int getRandomValue(String axis){
        Random rand  = new Random();
        if(axis.equals("x")){
            return rand.nextInt(TaskWarrior.WIDTH);
        }
        else if(axis.equals("y")){
            return rand.nextInt(TaskWarrior.HEIGHT);
        }
        return 1;
    }

    // adds minion at random value (except for player range)
    private void addMinion(PlayerChampion player, float deltaTime){
        if(spawnTimer >= ENEMY_SPAWN_FREQUENCY){
            Vector2 newPosition;
            // don't allow minions to spawn near player
            do{
                newPosition = new Vector2(getRandomValue("x"), getRandomValue("y"));
            }while(player.getForbiddenMinionSpawnRange().contains(newPosition));
            minions.add(new Minion((int)newPosition.x, (int)newPosition.y));
            spawnTimer = 0;
            nrOfMinions++;
        } else
            spawnTimer += deltaTime;
    }

    // UPDATE CAMERA METHODS

    // update camera position based on background limits
    private void updateCamera(PlayerChampion target){
        if(target.getPosition().x - camera.viewportWidth/2 * camera.zoom > 0 && target.getPosition().x + camera.viewportWidth/2 * camera.zoom < TaskWarrior.WIDTH){
            followPlayerX(target);
        }
        if(target.getPosition().y - camera.viewportHeight/2 * camera.zoom > 0 && target.getPosition().y + camera.viewportHeight/2 * camera.zoom< TaskWarrior.HEIGHT){
            followPlayerY(target);
        }
        camera.update();
    }

    // update camera position based on the player and axis
    private void followPlayerX(PlayerChampion target){
        camera.position.x = target.getPosition().x;
    }
    private void followPlayerY(PlayerChampion target){
        camera.position.y = target.getPosition().y;
    }

    // SHOW BORDER METHODS (for debug purposes)
    private void showBorders(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        showBordersForChampion(shapeRenderer, target);

        // show borders for minions
        for(int i = 0; i < nrOfMinions; i++){
            Rectangle minionRect = minions.get(i).getEnemyRectangle();
            Circle minionRange = minions.get(i).getMinionSenseRange();
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(minionRect.getX(), minionRect.getY(), minionRect.getWidth(), minionRect.getHeight());
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.circle(minionRange.x, minionRange.y, minionRange.radius);
        }
    }

    private void showBordersForChampion(ShapeRenderer shapeRenderer, PlayerChampion target){
        // border for champion
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(target.getPlayerRectangle().getX(), target.getPlayerRectangle().getY(), target.getPlayerRectangle().getWidth(), target.getPlayerRectangle().getHeight());

        // border for champion ability
        shapeRenderer.setColor(Color.RED);
        if(target.getState() == PlayerChampion.State.E && target.isEAttackTiming(true)){
            drawShape(target.getEAttackRange(), shapeRenderer);
        }
        if(target.getState() == PlayerChampion.State.Q && target.isQAttackTiming(true)){
            drawShape(target.getQAttackRange(), shapeRenderer);
        }
        if(target.getState() == PlayerChampion.State.W && target.isWAttackTiming(true)){
            drawShape(target.getWAttackRange(), shapeRenderer);
        }

        // border for minion spawn
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(target.getForbiddenMinionSpawnRange().x, target.getForbiddenMinionSpawnRange().y, target.getForbiddenMinionSpawnRange().radius);
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
