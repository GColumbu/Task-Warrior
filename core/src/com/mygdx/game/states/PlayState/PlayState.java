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
import com.mygdx.game.enemies.*;
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

    // game characters
    private List<Enemy> enemies;
    private List<Potion> potions;
    private final PlayerChampion target;
    private UserInterface userInterface;
    private Minimap minimap;

    // shape renderer for debug purposes
    ShapeRenderer shapeRenderer;

    // constructor
    public PlayState(TaskWarrior game){
        this.game = game;
        spawnTimer = 0;
        background = new Texture("assets/play screen/background.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, TaskWarrior.WIDTH, TaskWarrior.HEIGHT);
        enemies = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            //enemies.add(new Troll(getRandomValue("x"), getRandomValue("y")));
            enemies.add(getEnemy(getRandomValue("x"), getRandomValue("y")));
        }
        potions = new ArrayList<>();
        target = new Garen(TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT/2);
        shapeRenderer = new ShapeRenderer();
        userInterface = new UserInterface("assets/play screen/UI_template.png", target, camera);
        viewport = new ExtendViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT, camera);
        minimapReference = new FitViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT);
        minimap = new Minimap(new Texture("assets/play screen/minimap/minimap.png"));
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
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).getSprite().getTexture().dispose();
        }
        //dispose player
        target.getSprite().getTexture().dispose();
    }


    // methods utils

    private void update(float deltaTime) {
        target.update(deltaTime);
        for(int i = 0; i< enemies.size(); i++){
            enemies.get(i).update(target, deltaTime);
            if(enemies.get(i).isDyingAnimationFinished()){
                enemies.get(i).getSprite().getTexture().dispose();
                if(enemies.get(i) instanceof Runner) {
                    Random rd = new Random();
                    potions.add(new Potion(rd.nextBoolean(), enemies.get(i).getPosition().x, enemies.get(i).getPosition().y, 15));
                }
                enemies.remove(i);
                i--;
            }
        }
        for(int i = 0; i< enemies.size(); i++){
            enemies.get(i).move(target, enemies, deltaTime);
        }
        for(int i = 0; i< potions.size(); i++) {
            if (target.getPlayerRectangle().overlaps(potions.get(i).getBounds())){
                if(!potions.get(i).isArmor()){
                    target.incrementHealth(potions.get(i).getHealing());
                } else {
                    target.incrementArmor(potions.get(i).getHealing());
                }
                potions.remove(i);
            }
        }
        //showBorders();
        addMinion(target, deltaTime);
    }

    private void render() {
        // game objects
        drawGameViewport();

        // minimap
        minimap.draw(game.batch, minimapReference, target, enemies);
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
        for(int i = 0; i< potions.size(); i++){
            potions.get(i).draw(game.batch);
        }
        for(int i = 0; i < enemies.size(); i++) {
            game.batch.draw(enemies.get(i).getSprite(), enemies.get(i).getRelativePosition().x, enemies.get(i).getRelativePosition().y,
                    enemies.get(i).getSprite().getRegionWidth()/ 2,
                    enemies.get(i).getSprite().getRegionHeight() / 2, enemies.get(i).getSprite().getRegionWidth(),
                    enemies.get(i).getSprite().getRegionHeight(), 1, 1,
                    enemies.get(i).getHeading());
        }
        userInterface.draw(game.batch, camera, target);
        shapeRenderer.end();
        game.batch.end();
    }

    // ENEMY SPAWNING METHODS
    private Enemy getEnemy(int x, int y){
        Random rand  = new Random();
        int probability = rand.nextInt(1, 100);
        if(probability >= 0 && probability < 90){
            return new Minion(x, y);
        } else if (probability >= 90){
            return new Runner(x, y);
        }
        return null;
    }
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
            enemies.add(getEnemy((int)newPosition.x, (int)newPosition.y));
            spawnTimer = 0;
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
        for(int i = 0; i < enemies.size(); i++){
            Rectangle enemyRect = enemies.get(i).getEnemyRectangle();
            Circle enemyRange = enemies.get(i).getEnemySenseRange();
            Shape2D enemyAttack = enemies.get(i).getAttackRange();
            if(enemies.get(i).getCurrentState() != Enemy.State.ATTACK) {
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(enemyRect.getX(), enemyRect.getY(), enemyRect.getWidth(), enemyRect.getHeight());
            }
            if (enemies.get(i).getCurrentState() == Enemy.State.ATTACK){
                shapeRenderer.setColor(Color.RED);
                drawShape(enemyAttack, shapeRenderer);
            }
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.circle(enemyRange.x, enemyRange.y, enemyRange.radius);

            // troll specific behavior
            if(enemies.get(i) instanceof Troll){
                Circle trollCharge = ((Troll) enemies.get(i)).getTrollChargeRange();
                shapeRenderer.setColor(Color.FIREBRICK);
                shapeRenderer.circle(trollCharge.x, trollCharge.y, trollCharge.radius);
            }
        }

        // show border for potions
        for(int i = 0; i < potions.size(); i++){
            Rectangle potionRect = potions.get(i).getBounds();
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(potionRect.getX(), potionRect.getY(), potionRect.getWidth(), potionRect.getHeight());

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

        // border for runner behavior
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.circle(target.getRunnerBehaviorRange().x, target.getRunnerBehaviorRange().y, target.getRunnerBehaviorRange().radius);

        if(target instanceof Garen){
            Garen garen = (Garen) target;
            if(garen.getState() == PlayerChampion.State.W && !garen.getWAnimation().isBurst(garen.getStateTimer(),false)){
                shapeRenderer.setColor(Color.BLUE);
                shapeRenderer.circle(garen.getInvincibilityRange().x, garen.getInvincibilityRange().y, garen.getInvincibilityRange().radius);
            }
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
