package com.mygdx.game.players.garen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.mygdx.game.TaskWarrior;
import com.mygdx.game.players.*;
import com.mygdx.game.states.PlayerStatistic;

import java.util.List;


public class Garen extends PlayerChampion {
    protected Garen_W wAnimation;
    protected Garen_Q qAnimation;
    protected Garen_E eAnimation;
    protected Garen_DeathAnimation deathAnimation;

    public Garen(int x, int y, List<PlayerStatistic> statistics){
        super(x, y, statistics.get(0).getValue(), statistics.get(1).getValue(), statistics.get(2).getValue(), statistics.get(3).getValue(), statistics.get(4).getValue(), statistics.get(5).getValue());
        walkingAnimation = new WalkingAnimation("assets/play screen/garen/walk.png", 0.05f);
        wAnimation = new Garen_W("assets/play screen/garen/invincibility_burst.png", "assets/play screen/garen/invincibility_walk.png", "assets/play screen/garen/idle_w.png", "assets/play screen/garen/w_icon.png", 0.07f, 0.05f, "assets/sounds/garen/w_burst.mp3");
        wBasicAnimation = wAnimation;
        qAnimation = new Garen_Q("assets/play screen/garen/slash_combo_part1.png", "assets/play screen/garen/slash_combo_part2.png", "assets/play screen/garen/q_icon.png",0.07f, "assets/sounds/garen/q_slash.mp3");
        qBasicAnimation = qAnimation;
        eAnimation = new Garen_E("assets/play screen/garen/spin.png", "assets/play screen/garen/e_icon.png", 0.15f, 0.05f, "assets/sounds/garen/e_spin.mp3");
        eBasicAnimation = eAnimation;
        deathAnimation = new Garen_DeathAnimation("assets/play screen/garen/death.png", 0.15f);
        idleTextureRegion = new TextureRegion(new Texture("assets/play screen/garen/idle.png"));
        currentRegion = idleTextureRegion;
        currentState = State.STANDING;
        stateTimer = 0;

    }

    @Override
    public void update(float deltaTime) {
        updateCooldowns(deltaTime);
        setRelativePosition();
        setCurrentRegion(getCurrentFrame(deltaTime));
        setPlayerRectangle(new Rectangle(getIdleRelativePosition().x + 20, getIdleRelativePosition().y, getIdleTextureRegion().getRegionWidth() - 40, getIdleTextureRegion().getRegionHeight()));
        setForbiddenMinionSpawnRange(new Circle(position.x, position.y, 800));
        if(currentState == PlayerChampion.State.W && !wAnimation.isBurst(stateTimer,false))
            wAnimation.setInvincibilityRange(new Circle(position.x, position.y, 275));
        setRunnerBehaviorRange(new Circle(position.x, position.y, 450));
        movePlayer(deltaTime);
        updateArmor(deltaTime);
        addAbilitySoundEffects();
    }

    // calculates where the draw function should start based on the current state
    @Override
    protected void setRelativePosition() {
        currentState = getState();
        switch (currentState) {
            case DEATH:
                relativePosition = getDeathRelativePosition();
                break;
            case Q:
                relativePosition = getQSlashRelativePosition();
                break;
            case W:
                if (wAnimation.isBurst(stateTimer, false))
                    relativePosition = getWBurstRelativePosition();
                else
                    relativePosition = getIdleRelativePosition();
                break;
            case E:
                relativePosition = getESpinRelativePosition();
                break;
            case WALKING:
            case STANDING:
                relativePosition = getIdleRelativePosition();
                break;
        }
    }

    // MOVEMENT METHODS
    @Override
    protected void movePlayer(float deltaTime){
        // reset velocity vector
        velocity = new Vector2(0, 0);

        boolean isWBurst = wAnimation.isBurst(stateTimer, false);
        Vector2 idleRelativePosition = getIdleRelativePosition();
        int constantSpeed = (int)(deltaTime * speed);
        int qSpeedDifference = (int)(constantSpeed / 2f);
        int wSpeedDifference = (int)(constantSpeed / 3f);

        if(currentState != State.W || !isWBurst) {
            // movement on y axis
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (idleRelativePosition.y < TaskWarrior.HEIGHT - idleTextureRegion.getRegionHeight()) {
                    velocity.y = constantSpeed;
                    if (currentState == State.Q) {
                        velocity.y -= qSpeedDifference;
                    }
                    if (currentState == State.W && !isWBurst){
                        velocity.y += wSpeedDifference;
                    }
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (idleRelativePosition.y > 0) {
                    velocity.y = -constantSpeed;
                    if (currentState == State.Q) {
                        velocity.y += qSpeedDifference;
                    }
                    if (currentState == State.W && !isWBurst){
                        velocity.y -= wSpeedDifference;
                    }
                }
            }

            // movement on x axis
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (idleRelativePosition.x > 0) {
                    velocity.x = -constantSpeed;
                    if (currentState == State.Q) {
                        velocity.x += qSpeedDifference;
                    }
                     if (currentState == State.W && !isWBurst){
                        velocity.x -= wSpeedDifference;
                    }
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (idleRelativePosition.x < TaskWarrior.WIDTH - idleTextureRegion.getRegionWidth()) {
                    velocity.x = constantSpeed;
                    if (currentState == State.Q) {
                        velocity.x -= qSpeedDifference;
                    }
                    if (currentState == State.W && !isWBurst){
                        velocity.x += wSpeedDifference;
                    }
                }
            }
        }
        // if player is not moving record the last moving velocity
        if(!velocity.equals(Vector2.Zero)){
            previousMovingVelocity = velocity;
        }

        // record previous position to check for collision towards the minion
        previousX = position.x;
        previousY = position.y;

        // add velocity to position vector
        position.add(velocity);
    }

    // gets the player current texture frame based on the current state
    @Override
    protected TextureRegion getCurrentFrame(float deltaTime){
        TextureRegion region;
        switch(currentState){
            case DEATH:
                region = deathAnimation.getKeyFrame(stateTimer);
                break;
            case Q:
                region = qAnimation.getKeyFrame(stateTimer);
                break;
            case W:
                if (wAnimation.isBurst(stateTimer, false))
                    region = wAnimation.getBurstKeyFrame(stateTimer);
                else if (isMoving())
                    region = wAnimation.getWalkingKeyFrame(stateTimer);
                else
                    region = wAnimation.idleInvincibilityTextureRegion;
                break;
            case E:
                if (eAnimation.isSwordGrab(stateTimer))
                    region = eAnimation.getGrabKeyFrame(stateTimer);
                else
                    region = eAnimation.getSpinKeyFrame(stateTimer);
                break;
            case WALKING:
                region = walkingAnimation.getKeyFrame(stateTimer);
                break;
            default:
            case STANDING:
                region = idleTextureRegion;
                break;
        }
        if (previousState == currentState){
            stateTimer += deltaTime;
        } else
            stateTimer = 0;
        previousState = currentState;
        return region;
    }

    // player death methods
    @Override
    public boolean isDeathAnimationFinished(){
        return currentState == State.DEATH && deathAnimation.deathAnimation.isAnimationFinished(stateTimer);
    }
    protected Vector2 getDeathRelativePosition(){
        return new Vector2(position.x - deathAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - deathAnimation.getKeyFrameHeight(stateTimer) / 2);
    }

    // Q spell methods

    protected void playQSoundEffect(){
        int index = qAnimation.getKeyFrameIndex(stateTimer);
        Sound qSoundEffect = qAnimation.getSoundEffects().get(0);
        if(currentState == State.Q && index == 0){
            qSoundEffect.setVolume(qSoundEffect.play(), 0.03f);
        }
        if (currentState == State.Q && Gdx.input.isButtonJustPressed(Input.Keys.Q)){
            qSoundEffect.stop();
        }
    }

    protected Vector2 getQSlashRelativePosition() {
        return new Vector2(position.x - qAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - qAnimation.getKeyFrameHeight(stateTimer) / 2);
    }
    @Override
    public boolean isQAttackTiming(boolean forCollision){
        int index = qAnimation.getKeyFrameIndex(stateTimer);
        return index >=0 && index <=3 //first slash
               || index >= 7 && index <= 9 //second slash
               || index >= 12 && index <= 15 //third slash
               || index >= 17; //fourth slash
    }
    @Override
    public float getQAttackDamage() {
        return qAnimation.qAttackDamage;
    }
    @Override
    public Shape2D getQAttackRange(){
        // duration and limit of first slash
        if(qAnimation.isFirstSlash(stateTimer)){
            qAnimation.qAttackDamage = 5;
            return getQFirstPartAttackRange();
        }
        // duration and limit of the second (rotative) slash
        else if (qAnimation.isSecondSlash(stateTimer)){
            qAnimation.qAttackDamage = 3;
            return getQSecondPartAttackRange();
        }
        // duration and limit of third slash
        else if (qAnimation.isThirdSlash(stateTimer)) {
            qAnimation.qAttackDamage = 1.5f;
            return getQThirdPartAttackRange();
        }
        return null;
    }

    // q first part
    private Shape2D getQFirstPartAttackRange(){
        float x = getIdleRelativePosition().x;
        float y = getIdleRelativePosition().y;
        if(getHeading() == 0) {
            return new Rectangle(x + 35, y - 125, 200, 310);
        } else if(getHeading() == 45) {
            float[] vertices = {x + 156, y - 82, x + 286, y + 48, x + 70, y + 268, x - 65, y + 128};
            return new Polygon(vertices);
        } else if (getHeading() == 90) {
            return new Rectangle(x - 100, y + 35, 305, 175);
        } else if(getHeading() == 135) {
            float[] vertices = {x - 55, y - 75, x - 180, y + 50, x + 40, y + 268, x + 160, y + 140};
            return new Polygon(vertices);
        } else if (getHeading() == 180) {
            return new Rectangle(x - 125, y - 125, 190, 310);
        } else if (getHeading() == 225) {
            float[] vertices = {x - 55, y + 138, x - 180, y + 10, x + 40, y - 186, x + 156, y - 55};
            return new Polygon(vertices);
        } else if (getHeading() == 270) {
            return new Rectangle( x - 100,y - 150, 305, 195);
        } else if (getHeading() == 315) {
            float[] vertices = {x - 55, y - 75, x + 76, y - 186, x + 282, y + 20, x + 160, y + 140};
            return new Polygon(vertices);
        }
        return null;
    }

    // q second part
    private Shape2D getQSecondPartAttackRange(){
        if(getHeading() == 0) {
            return new Circle(position.x + 15, position.y - 5, 175);
        } else if(getHeading() == 45) {
            return new Circle(position.x + 13, position.y + 6, 175);
        } else if (getHeading() == 90) {
            return new Circle(position.x + 9, position.y + 15, 175);
        } else if(getHeading() == 135) {
            return new Circle(position.x - 5, position.y + 15, 175);
        } else if (getHeading() == 180) {
            return new Circle(position.x - 13, position.y + 7, 175);
        } else if(getHeading() == 225) {
            return new Circle(position.x - 13, position.y - 5, 175);
        } else if (getHeading() == 270) {
            return new Circle(position.x - 7, position.y - 12, 175);
        } else if(getHeading() == 315) {
            return new Circle(position.x + 3, position.y - 12, 175);
        }
        return null;
    }

    // q third part
    private Shape2D getQThirdPartAttackRange(){
        float x = getIdleRelativePosition().x;
        float y = getIdleRelativePosition().y;
        if(getHeading() == 0) {
            float[] vertices = {x + 35, y - 485, x + 250, y - 450, x + 420, y - 370, x + 595, y - 150, x + 625, y + 50, x + 605, y + 200, x + 515,
                    y + 375, x + 450, y + 440, x + 390, y + 470, x + 320, y + 520, x + 170, y + 560, x + 50, y + 550};
            return new Polygon(vertices);
        }  if(getHeading() == 45) {
            float[] vertices = {x + 410, y - 345, x + 515, y - 220, x + 572, y - 100, x + 610, y + 50, x + 605, y + 200, x + 515, y + 375, x + 460,
                    y + 440, x + 390, y + 500, x + 320, y + 550, x + 170, y + 590, x, y + 590, x - 140, y + 540, x - 325, y + 385};
            return new Polygon(vertices);
        } else if (getHeading() == 90) {
            float[] vertices = {x + 575, y + 15, x + 560, y + 200, x + 520, y + 300, x + 480, y + 375, x + 450, y + 440, x + 390, y + 500, x + 320,
                    y + 540, x + 170, y + 590, x, y + 605, x - 140, y + 565, x - 270, y + 500, x - 430, y + 300, x - 465, y + 150, x - 457, y + 15};
            return new Polygon(vertices);
        } else if(getHeading() == 135) {
            float[] vertices = {x + 430, y + 385, x + 280, y + 510, x + 100, y + 575, x, y + 587, x - 140, y + 565, x - 270, y + 500, x - 430, y + 330,
                    x - 490, y + 150, x - 490, y - 75, x - 360, y - 270, x - 300, y - 320};
            return new Polygon(vertices);
        } else if (getHeading() == 180) {
            float[] vertices = {x + 70, y + 550, x -50, y + 540, x - 140, y + 520, x - 270, y + 475, x - 430, y + 330, x - 490, y + 150, x - 490, y - 75,
                    x - 425, y - 270, x - 250, y - 420, x - 100, y - 470, x + 70, y - 470};
            return new Polygon(vertices);
        } else if(getHeading() == 225) {
            float[] vertices = {x - 300, y + 415, x - 430, y + 230, x - 470, y + 150, x - 490, y - 75, x - 425, y - 270, x - 250, y - 430, x - 100, y - 495,
                    x + 70, y - 510, x + 230, y - 480, x + 330, y - 400, x + 430, y - 310};
            return new Polygon(vertices);
        } else if (getHeading() == 270) {
            float[] vertices = {x - 470, y + 50, x - 445, y - 150, x - 405, y - 270, x - 250, y - 430, x - 100, y - 510, x + 70, y - 530, x + 230, y - 505,
                    x + 330, y - 440, x + 470, y - 310, x + 550, y - 150, x + 570, y + 50};
            return new Polygon(vertices);
        } else if (getHeading() == 315) {
            float[] vertices = {x - 320, y - 320, x - 225, y - 400, x - 100, y - 495, x + 70, y - 520, x + 230, y - 505,
                    x + 345, y - 440, x + 495, y - 310, x + 587, y - 150, x + 597, y + 50, x + 555, y + 205, x + 505, y + 315, x + 400, y + 400};
            return new Polygon(vertices);
        }
        return null;
    }

    // W spell

    protected void playWBurstSoundEffect(){
        int index = wAnimation.getKeyFrameIndex(stateTimer);
        Sound wBurstSoundEffect = wAnimation.getSoundEffects().get(0);
        if(currentState == State.W && index == 6){
            wBurstSoundEffect.setVolume(wBurstSoundEffect.play(), 20f);
        }
    }
    protected Vector2 getWBurstRelativePosition() {
        return new Vector2(position.x - wAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - wAnimation.getKeyFrameHeight(stateTimer) / 2);
    }
    public Garen_W getWAnimation() {
        return wAnimation;
    }
    public Circle getInvincibilityRange(){
        return  wAnimation.invincibilityRange;
    }
    @Override
    public boolean isWAttackTiming(boolean forCollision){
        return wAnimation.isBurst(stateTimer, forCollision);
    }
    @Override
    public float getWAttackDamage() {
        return wAnimation.wAttackDamage;
    }
    @Override
    public Circle getWAttackRange(){
        return new Circle(position.x, position.y, 270);
    }

    // E spell

    protected void playESpinSoundEffect(){
        int index = eAnimation.getGrabKeyFrameIndex(stateTimer);
        Sound eSpinSoundEffect = eAnimation.getSoundEffects().get(0);
        if(currentState == State.E && index == 0){
            Long eSoundEffectId = eSpinSoundEffect.play();
            eSpinSoundEffect.setVolume(eSoundEffectId, 0.03f);
        }
        if (currentState == State.E && Gdx.input.isButtonJustPressed(Input.Keys.E)){
            eSpinSoundEffect.stop();
        }
    }

    protected Vector2 getESpinRelativePosition() {
        return new Vector2(position.x - eAnimation.getKeyFrameWidth(stateTimer) / 2, position.y - eAnimation.getKeyFrameHeight(stateTimer) / 2);
    }
    @Override
    public boolean isEAttackTiming(boolean forCollision){
        int index = eAnimation.getSpinKeyFrameIndex(stateTimer);
        return  index >=0 && index <=2;
    }
    @Override
    public float getEAttackDamage() {
        return eAnimation.eSpinAttackDamage;
    }
    @Override
    public Circle getEAttackRange(){
        return new Circle(position.x, position.y, 310);
    }

    // add sound effect logic
    protected void addAbilitySoundEffects(){
        playQSoundEffect();
        playWBurstSoundEffect();
        playESpinSoundEffect();
    }

    public void disposeSoundEffects(){
        qAnimation.disposeSoundEffects();
        wAnimation.disposeSoundEffects();
        eAnimation.disposeSoundEffects();
    }
}
