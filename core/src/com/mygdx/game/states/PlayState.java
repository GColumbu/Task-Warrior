package com.mygdx.game.states;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.enemies.Enemy;
import com.mygdx.game.enemies.Minion;
import com.mygdx.game.players.Player;
import com.mygdx.game.players.PlayerChampion;

import java.util.ArrayList;
import java.util.List;

public class PlayState extends State{
    private static final int NR_OF_MINIONS = 2;
    private static final float REPULSION_FACTOR = 0.5f;
    private List<Enemy> minions;
    private PlayerChampion target;
    private Texture background;

    protected PlayState(GameStateManager gsm){
        super(gsm);
        background = new Texture("background.png");
        minions = new ArrayList<>();
        for(int i=0; i<NR_OF_MINIONS; i++){
            minions.add(new Minion(i * 100, i * 300));
        }
        target = new Player(TaskWarrior.WIDTH/4, TaskWarrior.HEIGHT/2);
        camera.setToOrtho(false, TaskWarrior.WIDTH/2, TaskWarrior.HEIGHT);
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
        }
        //avoidEnemyCollisions(minions, deltaTime);
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
        batch.end();
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

    private Vector2 calculateRepulsionVector(Enemy enemy1, Enemy enemy2, float deltaTime) {
        Vector2 enemy1ToEnemy2 = enemy2.getPosition().cpy().sub(enemy1.getPosition());
        float distance = enemy1ToEnemy2.len();
        Vector2 direction = enemy1ToEnemy2.nor();
        float repulsionMagnitude = deltaTime * REPULSION_FACTOR / (distance * distance);
        return direction.scl(repulsionMagnitude);
    }
}
