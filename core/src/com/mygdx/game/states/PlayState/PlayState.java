package com.mygdx.game.states.PlayState;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.enemies.*;
import com.mygdx.game.players.garen.Garen;
import com.mygdx.game.players.PlayerChampion;
import com.mygdx.game.states.GameOverState;
import com.mygdx.game.states.PlayState.UI.UserInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PlayState implements Screen {

    private static final int ENEMY_SPAWN_FREQUENCY = 2;

    // game utils
    private final TaskWarrior game;

    private final Music gameMusic;
        // Shader
    private ShaderProgram shaderProgram;

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
    private Integer bestSkullsNumber;
    HashMap<Class<?>, Integer> enemySkulls = new HashMap<Class<?>, Integer>() {{
        put(Minion.class, 100);
        put(Runner.class, 150);
        put(Troll.class, 200);
    }};

    // Game Characters
    private final PlayerChampion target;
    private List<Enemy> enemies;
        // minion
    private final EnemyTextures minionTextures;

        // runner
    private final EnemyTextures runnerTextures;

        // troll
    private final EnemyTextures trollTextures;

    // potions
    private List<Potion> potions;
        //potion textures
    private final Texture armorPotion;
    private final Texture healthPotion;

    // ShapeRenderer for DEBUG purposes
    ShapeRenderer shapeRenderer;

    // constructor  //TODO: pass a difficulty class to initiate the player upgrades
    public PlayState(TaskWarrior game, Integer bestScore){
        this.game = game;
        this.game.batch = new SpriteBatch();
        this.background = new Texture("assets/play screen/background.png");
        this.gameMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/sounds/in_game_music.mp3"));
        this.gameMusic.setLooping(true);
        this.gameMusic.play();
        this.gameMusic.setVolume(0.03f);
        this.skulls = new Skulls("assets/play screen/skull.png", "assets/fonts/pixel_font.ttf");
        this.bestSkullsNumber = bestScore;

        // player
        this.target = new Garen(TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT/2);

        // enemies
        this.enemies = new ArrayList<>();
        this.minionTextures = new EnemyTextures(
                new TextureRegion(new Texture("assets/play screen/minion/minion_idle.png")),
                new EnemyAnimation("assets/play screen/minion/minion_walk.png", 0.1f, 12),
                new EnemyAnimation("assets/play screen/minion/minion_walk_damage.png", 0.1f, 12),
                new EnemyAnimation("assets/play screen/minion/minion_attack.png", 0.08f, 7),
                new EnemyAnimation("assets/play screen/minion/minion_attack_damage.png", 0.08f, 7),
                new EnemyAnimation("assets/play screen/minion/minion_death.png", 0.07f, 4));
        this.runnerTextures = new EnemyTextures(
                new TextureRegion(new Texture("assets/play screen/runner/runner_idle.png")),
                new EnemyAnimation("assets/play screen/runner/runner_walk.png", 0.07f, 12),
                new EnemyAnimation("assets/play screen/runner/runner_walk_dmg.png", 0.07f, 12),
                null, null,
                new EnemyAnimation("assets/play screen/runner/runner_death.png", 0.07f, 4));
        this.trollTextures = new EnemyTextures(
                new TextureRegion(new Texture("assets/play screen/troll/troll_idle.png")),
                new EnemyAnimation("assets/play screen/troll/troll_walk.png", 0.2f, 11),
                new EnemyAnimation("assets/play screen/troll/troll_walk_damage.png", 0.2f, 11),
                new EnemyAnimation("assets/play screen/troll/troll_attack.png", 0.2f, 10),
                new EnemyAnimation("assets/play screen/troll/troll_attack_damage.png", 0.2f, 10),
                new EnemyAnimation("assets/play screen/troll/troll_death.png", 0.09f, 5));

        // potions
        this.potions = new ArrayList<>();
        this.armorPotion = new Texture("assets/play screen/potions/armor_potion.png");
        this.healthPotion = new Texture("assets/play screen/potions/health_potion.png");

        // default enemies on screen
        for(int i = 0; i < 5; i++){
            this.enemies.add(getEnemy(getRandomValue("x"), getRandomValue("y")));
        }
        this.shapeRenderer = new ShapeRenderer(); // for debug purposes
        this.spawnTimer = 0;
        this.skullsNumber = 0;

        // camera and viewports
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, TaskWarrior.WIDTH, TaskWarrior.HEIGHT);
        this.viewport = new ExtendViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT, this.camera);
        this.minimapReference = new FitViewport(TaskWarrior.WIDTH, TaskWarrior.HEIGHT);

        // UI
        this.userInterface = new UserInterface("assets/play screen/UI_template.png", target);
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
        // dispose background
        background.dispose();
        // dispose UI
        userInterface.dispose();
        // dispose minimap
        minimap.dispose();
        // dispose minions
        minionTextures.dispose();
        runnerTextures.dispose();
        trollTextures.dispose();
        // dispose potions
        armorPotion.dispose();
        healthPotion.dispose();
        //dispose shader
        shaderProgram.dispose();
        // dispose player
        target.getSprite().getTexture().dispose();
        //dispose music
        gameMusic.dispose();
    }


    // methods utils

    private void update(float deltaTime) {
        target.update(deltaTime);
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).update(target, deltaTime);
            if(enemies.get(i).isDyingAnimationFinished()){
                addSkulls(enemies.get(i));
                if(enemies.get(i) instanceof Runner) {
                    if (isArmor())
                        potions.add(new Potion(true, enemies.get(i).getPosition().x, enemies.get(i).getPosition().y, 15, armorPotion, "assets/sounds/potion_pickup.mp3"));
                    else
                        potions.add(new Potion(false, enemies.get(i).getPosition().x, enemies.get(i).getPosition().y, 15, healthPotion, "assets/sounds/potion_pickup.mp3"));
                }
                enemies.remove(i);
                i--;
            }
        }
        for(int i = 0; i< enemies.size(); i++){
            enemies.get(i).move(target, enemies, deltaTime);
        }
        for(int i = 0; i < potions.size(); i++) {
            if (target.getPlayerRectangle().overlaps(potions.get(i).getBounds())){
                if(potions.get(i).isArmor()){
                    target.incrementArmor(potions.get(i).getHealing());
                    potions.get(i).getPickupSoundEffect().play();
                } else {
                    target.incrementHealth(potions.get(i).getHealing());
                    potions.get(i).getPickupSoundEffect().play();
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

        if(target.isDeathAnimationFinished()){
            game.batch.dispose();
            gameMusic.stop();
            game.setScreen( new GameOverState(game, skullsNumber, bestSkullsNumber));
        }
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
            return new Minion(x, y, minionTextures);
        } else if (probability > minionProbability && probability <= minionRunnerProbability){
            return new Runner(x, y, runnerTextures);
        } else if (probability > minionRunnerProbability && probability <= 100)
            return new Troll(x, y, trollTextures);
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

        // update camera position based on arena limits
    private void updateCamera(PlayerChampion target){ // TODO: arena limits not working with the new camera lag
        if(target.getPosition().x - camera.viewportWidth/2 * camera.zoom > 0 && target.getPosition().x + camera.viewportWidth/2 * camera.zoom < TaskWarrior.WIDTH){
            followPlayerX(target);
        }
        if(target.getPosition().y - camera.viewportHeight/2 * camera.zoom > 0 && target.getPosition().y + camera.viewportHeight/2 * camera.zoom< TaskWarrior.HEIGHT){
            followPlayerY(target);
        }
        viewport.getCamera().update();
    }

        // update camera position based on the player and axis
    private void followPlayerX(PlayerChampion target){
        Vector2 cameraPosition = new Vector2(viewport.getCamera().position.x, 0);
        cameraPosition.lerp(new Vector2(target.getPosition().x, 0), 0.07f);
        viewport.getCamera().position.x = (int)cameraPosition.x;
    }
    private void followPlayerY(PlayerChampion target){
        Vector2 cameraPosition = new Vector2(0, viewport.getCamera().position.y);
        cameraPosition.lerp(new Vector2(0, target.getPosition().y), 0.07f);
        viewport.getCamera().position.y = (int)cameraPosition.y;
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
