package com.mygdx.game.states.PlayState;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.players.PlayerChampion;

public class MyInputProcessor implements InputProcessor {

    private OrthographicCamera camera;

    private PlayerChampion player;

    public MyInputProcessor(OrthographicCamera camera, PlayerChampion player) {
        this.camera = camera;
        this.player = player;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        // Adjust the camera's zoom level based on the amount of scrolling
        float zoomAmount = v1 * 0.1f;
        camera.zoom += zoomAmount;

        // Set the minimum and maximum zoom levels
        float maxZoom = 1.0f;
        float minZoom = 0.5f;
        if (camera.zoom > maxZoom) {
            camera.zoom = maxZoom;
        } else if (camera.zoom < minZoom) {
            camera.zoom = minZoom;
        }

        // Change camera position when zooming would show outside map
        updateCamera(player);
        return true;
    }

    //update camera position based on background limits
    public void updateCamera(PlayerChampion target){
        //don't center on player when zooming will show the outside of the map
            // x axis
        if(target.getPosition().x - camera.viewportWidth/2 * camera.zoom < 0)
            camera.position.x =  camera.viewportWidth/2 * camera.zoom;
        else if(target.getPosition().x + camera.viewportWidth/2 * camera.zoom > TaskWarrior.WIDTH)
            camera.position.x = TaskWarrior.WIDTH - camera.viewportWidth/2 * camera.zoom;

            // y axis
        if(target.getPosition().y - camera.viewportHeight/2 * camera.zoom < 0)
            camera.position.y = camera.viewportHeight/2 * camera.zoom;
        else if(target.getPosition().y + camera.viewportHeight/2 * camera.zoom > TaskWarrior.HEIGHT)
            camera.position.y = TaskWarrior.HEIGHT - camera.viewportHeight/2 * camera.zoom;
        camera.update();
    }
}
