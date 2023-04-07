package com.mygdx.game.states.PlayState;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.enemies.Enemy;
import com.mygdx.game.enemies.Minion;
import com.mygdx.game.players.garen.Garen;
import com.mygdx.game.players.PlayerChampion;
import com.mygdx.game.states.GameStateManager;
import com.mygdx.game.states.State;

import java.util.ArrayList;
import java.util.List;

public class PlayState extends State {
    private static int NR_OF_MINIONS = 1;
    private static final float REPULSION_FACTOR = 0.5f;
    private List<Enemy> minions;
    private Garen target;
    private Texture background;
    private UserInterface userInterface;

    private HealthBar healthBar;

    ShapeRenderer borderShapeRenderer;
    ShapeRenderer healthShapeRenderer;
    public PlayState(GameStateManager gsm){
        super(gsm);
        background = new Texture("background.png");
        minions = new ArrayList<>();
        for(int i=0; i<NR_OF_MINIONS; i++){
            minions.add(new Minion(i * 100, i * 300));
        }
        target = new Garen(TaskWarrior.WIDTH/4, TaskWarrior.HEIGHT/2);
        camera.setToOrtho(false, TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT);
        borderShapeRenderer = new ShapeRenderer();
        healthShapeRenderer = new ShapeRenderer();
        userInterface = new UserInterface("assets/UI bar.png", target.getQ_Animation().getAttackIcon(), target.getW_Animation().getAttackIcon(), target.getE_Animation().getAttackIcon());
        healthBar = new HealthBar(19, 46, 23, target.MAX_HEALTH);
    }
    @Override
    protected void handleInput() {

    }

    @Override
    protected void update(float deltaTime) {
        handleInput();
        target.update(deltaTime);
        for(int i=0; i<NR_OF_MINIONS; i++){
            minions.get(i).update(target, deltaTime);
            if(minions.get(i).getHealth() <= 0){
                minions.remove(i);
                NR_OF_MINIONS--;
                i--;
            }
        }
        showBorders();
        updateCamera(target);
    }

    @Override
    protected void render(SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(target.getSprite(), target.getRelativePosition().x, target.getRelativePosition().y,
                target.getSprite().getRegionWidth() / 2,
                target.getSprite().getRegionHeight() / 2, target.getSprite().getRegionWidth(),
                target.getSprite().getRegionHeight(), 1, 1,
                target.getHeading());
        for(int i=0; i<NR_OF_MINIONS; i++) {
            batch.draw(minions.get(i).getSprite(), minions.get(i).getRelativePosition().x, minions.get(i).getRelativePosition().y,
                    minions.get(i).getSprite().getRegionWidth()/ 2,
                    minions.get(i).getSprite().getRegionHeight() / 2, minions.get(i).getSprite().getRegionWidth(),
                    minions.get(i).getSprite().getRegionHeight(), 1, 1,
                    minions.get(i).getHeading());
        }
        userInterface.draw(batch, camera);
        healthBar.draw(healthShapeRenderer, target.getHealth(), userInterface.getUIPosition(camera), camera);
        batch.end();
        borderShapeRenderer.end();
        healthShapeRenderer.end();
    }

    @Override
    protected void dispose() {
        //dispose minions
        for(int i=0; i<NR_OF_MINIONS; i++){
            minions.get(i).getSprite().getTexture().dispose();
        }
        //dispose player
        target.getSprite().getTexture().dispose();
    }

    //update camera position based on background limits
    private void updateCamera(PlayerChampion target){
        if((target.getRelativePosition().x + target.getSprite().getRegionWidth()/2) - camera.viewportWidth/2 > 0 && (target.getRelativePosition().x + target.getSprite().getRegionWidth()/2) + camera.viewportWidth/2 < TaskWarrior.WIDTH){
            followPlayerX(target);
        }
        if((target.getRelativePosition().y + target.getSprite().getRegionHeight()/2) - camera.viewportHeight/2 > 0 && (target.getRelativePosition().y + target.getSprite().getRegionHeight()/2) + camera.viewportHeight/2 < TaskWarrior.HEIGHT){
            followPlayerY(target);
        }
    }

    //update camera position based on the player and axis
    private void followPlayerX(PlayerChampion target){
        camera.position.x = target.getRelativePosition().x + target.getSprite().getRegionWidth()/2;
        camera.update();
    }
    private void followPlayerY(PlayerChampion target){
        camera.position.y = target.getRelativePosition().y + target.getSprite().getRegionHeight()/2;
        camera.update();
    }

    private void avoidEnemyCollisions(List<Enemy> enemies, float deltaTime) {
        for (int i = 0; i < enemies.size(); i++) {
            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy enemy1 = enemies.get(i);
                Enemy enemy2 = enemies.get(j);
                if (enemy1.getEnemyRectangle().overlaps(enemy2.getEnemyRectangle())) {
                    Vector2 repulsionVector = calculateRepulsionVector(enemy1, enemy2, deltaTime);
                    Vector2 enemy1Velocity = enemy1.getVelocity().cpy().add(repulsionVector);
                    Vector2 enemy2Velocity = enemy2.getVelocity().cpy().sub(repulsionVector);
                    enemy1.getPosition().add(enemy1Velocity);
                    enemy2.getPosition().add(enemy2Velocity);
                }
            }
        }
    }

    private void drawHealthBar(){
        healthShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float playerHealth = target.getHealth();
        Rectangle healthBar = new Rectangle(10, 10, 5 * playerHealth, 50);
        if(playerHealth > 60)
            healthShapeRenderer.setColor(Color.GREEN);
        else if (playerHealth <= 60 && playerHealth > 30)
            healthShapeRenderer.setColor(Color.ORANGE);
        else
            healthShapeRenderer.setColor(Color.RED);
        healthShapeRenderer.rect(healthBar.getX(), healthBar.getY(), healthBar.getWidth(), healthBar.getHeight());
    }

    private Vector2 calculateRepulsionVector(Enemy enemy1, Enemy enemy2, float deltaTime) {
        Vector2 enemy1ToEnemy2 = enemy2.getPosition().cpy().sub(enemy1.getPosition());
        float distance = enemy1ToEnemy2.len();
        Vector2 direction = enemy1ToEnemy2.nor();
        float repulsionMagnitude = deltaTime * REPULSION_FACTOR / (distance * distance);
        return direction.scl(repulsionMagnitude);
    }

    private void showBorders(){
        borderShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        borderShapeRenderer.setProjectionMatrix(camera.combined);
        // show borders for champions
        if(target instanceof Garen)
            showBordersForGaren(borderShapeRenderer, (Garen)target);
        //  ... other champions ...

        // show borders for minions
        for(int i=0; i<NR_OF_MINIONS; i++){
            Rectangle minionRect = minions.get(i).getEnemyRectangle();
            borderShapeRenderer.rect(minionRect.getX(), minionRect.getY(), minionRect.getWidth(), minionRect.getHeight());
        }
    }

    private void showBordersForGaren(ShapeRenderer shapeRenderer, Garen target){
        // border for champion
        shapeRenderer.rect(target.getPlayerRectangle().getX(), target.getPlayerRectangle().getY(), target.getPlayerRectangle().getWidth(), target.getPlayerRectangle().getHeight());

        // border for champion ability
        if(target.getState() == PlayerChampion.State.E){
            shapeRenderer.circle(target.getEAttackRange().x, target.getEAttackRange().y, target.getEAttackRange().radius);
        }
        if(target.getState() == PlayerChampion.State.Q ){
            if(target.getQAttackRange() instanceof Rectangle){
                Rectangle rectangle = (Rectangle)target.getQAttackRange();
                shapeRenderer.rect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
            } else if(target.getQAttackRange() instanceof Polygon){
                Polygon polygon = (Polygon)target.getQAttackRange();
                shapeRenderer.polygon(polygon.getVertices());
            } else if (target.getQAttackRange() instanceof Circle){
                Circle circle = (Circle)target.getQAttackRange();
                shapeRenderer.circle(circle.x, circle.y, circle.radius);
            }
        }
        if(target.getState() == PlayerChampion.State.W && target.getW_Animation().isBurst(target.getStateTimer(), true)){
            shapeRenderer.circle(target.getWAttackRange().x, target.getWAttackRange().y, target.getWAttackRange().radius);
        }
    }
}
