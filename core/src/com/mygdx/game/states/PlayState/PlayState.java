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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PlayState implements Screen {

    private static final int ENEMY_SPAWN_FREQUENCY = 2;

    // game utils
    private final TaskWarrior game;

        // UI and textures
    private final Texture background;
    private UserInterface userInterface;
    private Minimap minimap;
    private Skulls skulls;

        // camera and viewports
    protected Viewport viewport;
    protected Viewport minimapReference;
    private final OrthographicCamera camera;

        // spawn variables
    private float spawnTimer;
    HashMap<Class<?>, Integer> spawnProbabilities = new HashMap<Class<?>, Integer>() {{
        put(Minion.class, 75);
        put(Runner.class, 15);
        put(Troll.class, 10);
    }};

        // skulls variables
    private Integer skullsNumber;
    HashMap<Class<?>, Integer> enemySkulls = new HashMap<Class<?>, Integer>() {{
        put(Minion.class, 100);
        put(Runner.class, 150);
        put(Troll.class, 200);
    }};

    // Game Characters
    private final PlayerChampion target;
    private List<Enemy> enemies;
    private List<Potion> potions;

    // ShapeRenderer for DEBUG purposes
    ShapeRenderer shapeRenderer;

    // constructor  //TODO: pass a difficulty class to initiate the player upgrades
    public PlayState(TaskWarrior game){
        this.game = game;
        this.background = new Texture("assets/play screen/background.png");
        this.skulls = new Skulls("assets/play screen/skull.png");
        this.enemies = new ArrayList<>();
        this.potions = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            this.enemies.add(getEnemy(getRandomValue("x"), getRandomValue("y")));
        }
        this.target = new Garen(TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT/2);
        this.shapeRenderer = new ShapeRenderer(); // for debug purposes
        this.spawnTimer = 0;
        this.skullsNumber = 0;

        // camera and viewports
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, TaskWarrior.WIDTH, TaskWarrior.HEIGHT);
        this.viewport = new ExtendViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT, this.camera);
        this.minimapReference = new FitViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT);

        // UI
        this.userInterface = new UserInterface("assets/play screen/UI_template.png", target, camera);
        this.minimap = new Minimap(new Texture("assets/play screen/minimap/minimap.png"));

        // set input processor
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
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).update(target, deltaTime);
            if(enemies.get(i).isDyingAnimationFinished()){
                addSkulls(enemies.get(i));
                if(enemies.get(i) instanceof Runner) {
                    potions.add(new Potion(isArmor(), enemies.get(i).getPosition().x, enemies.get(i).getPosition().y, 15));
                }
                enemies.get(i).getSprite().getTexture().dispose();
                enemies.remove(i);
                i--;
            }
        }
        for(int i = 0; i< enemies.size(); i++){
            enemies.get(i).move(target, enemies, deltaTime);
        }
        for(int i = 0; i < potions.size(); i++) {
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
        for(int i = 0; i < potions.size(); i++){
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
        skulls.draw(game.batch, camera, skullsNumber);
        shapeRenderer.end();
        game.batch.end();
    }

    // ENEMY SPAWNING METHODS
    private Enemy getEnemy(int x, int y){
        Random rand  = new Random();
        int probability = rand.nextInt(1, 100);
        int minionProbability = spawnProbabilities.get(Minion.class);
        int minionRunnerProbability = minionProbability + spawnProbabilities.get(Runner.class);
        if(probability >= 0 && probability <= minionProbability){
            return new Minion(x, y);
        } else if (probability > minionProbability && probability <= minionRunnerProbability){
            return new Runner(x, y);
        } else if (probability > minionRunnerProbability && probability <= 100)
            return new Troll(x, y);
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
            } while(player.getForbiddenMinionSpawnRange().contains(newPosition));
            enemies.add(getEnemy((int)newPosition.x, (int)newPosition.y));
            spawnTimer = 0;
        } else
            spawnTimer += deltaTime;
    }

    // POTION SPAWNING METHODS
    private boolean isArmor(){
        Random rd = new Random();
        return rd.nextBoolean();
    }

    // SKULLS METHODS
    private void addSkulls(Enemy enemy){
        if(enemy instanceof Minion){
            this.skullsNumber += enemySkulls.get(Minion.class);
        } else if(enemy instanceof Runner){
            this.skullsNumber += enemySkulls.get(Runner.class);
        } else if(enemy instanceof Troll){
            this.skullsNumber += enemySkulls.get(Troll.class);
        }
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
        viewport.getCamera().update();
    }

        // update camera position based on the player and axis
    private void followPlayerX(PlayerChampion target){ //TODO: camera smoothness breaks the player colliding with map margins
        Vector2 cameraPosition = new Vector2(viewport.getCamera().position.x, 0);
        cameraPosition.lerp(new Vector2(target.getPosition().x, 0), 0.07f);
        viewport.getCamera().position.x = cameraPosition.x;
    }
    private void followPlayerY(PlayerChampion target){
        Vector2 cameraPosition = new Vector2(0, viewport.getCamera().position.y);
        cameraPosition.lerp(new Vector2(0, target.getPosition().y), 0.07f);
        viewport.getCamera().position.y = cameraPosition.y;
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
                Circle targetPosition = new Circle(((Troll) enemies.get(i)).getAttackTarget().x , ((Troll) enemies.get(i)).getAttackTarget().y , 50);
                shapeRenderer.setColor(Color.FIREBRICK);
                shapeRenderer.circle(trollCharge.x, trollCharge.y, trollCharge.radius);
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.circle(targetPosition.x, targetPosition.y, targetPosition.radius);
            }
        }

        // show border for potions
        for(int i = 0; i < potions.size(); i++){
            Rectangle potionRect = potions.get(i).getBounds();
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(potionRect.getX(), potionRect.getY(), potionRect.getWidth(), potionRect.getHeight());

        }
    }

    // border for champion texture
    private void showBordersForChampion(ShapeRenderer shapeRenderer, PlayerChampion target){
        // border for champion texture
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
