package ru.kizup.minibox2dgame.model.newtank.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.model.newtank.TankView;
import ru.kizup.minibox2dgame.model.turret.EnemyTankTurret;
import ru.kizup.minibox2dgame.model.turret.PlayerTankTurret;

import static ru.kizup.minibox2dgame.model.newtank.Tank.ACC_ACCELERATE;
import static ru.kizup.minibox2dgame.model.newtank.Tank.ACC_BRAKE;
import static ru.kizup.minibox2dgame.model.newtank.Tank.ACC_NONE;
import static ru.kizup.minibox2dgame.model.newtank.Tank.STEER_LEFT;
import static ru.kizup.minibox2dgame.model.newtank.Tank.STEER_NONE;
import static ru.kizup.minibox2dgame.model.newtank.Tank.STEER_RIGHT;
import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_EXIST;
import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_NONE;

/**
 * Created by yks-11 on 11/21/17.
 */

public class TankPlayerView extends TankView implements TankPlayerContact.View{

    private TankPlayerContact.Presenter presenterPlayer;

    public TankPlayerView(TankPlayerContact.Presenter presenter, World world) {
        super(presenter, world);
        this.presenterPlayer = presenter;

        this.presenterPlayer.setView(this);
    }

    @Override
    public void update(float delta, int hitPoints, int accelerate, float maxSpeed, int steer, float power, float koefRotation, long shootTime, long cooldownTime, int bullet) {
        super.update(delta, hitPoints, accelerate, maxSpeed, steer, power, koefRotation, shootTime, cooldownTime, bullet);

    }

    @Override
    public void handleInput(int accelerate, int steer, int bullet) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            presenterPlayer.setAccelerate(ACC_ACCELERATE);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            presenterPlayer.setAccelerate(ACC_BRAKE);
        } else {
            presenterPlayer.setAccelerate(ACC_NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            presenterPlayer.setSteer(STEER_LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            presenterPlayer.setSteer(STEER_RIGHT);
        } else {
            presenterPlayer.setSteer(STEER_NONE);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            presenterPlayer.setAccelerate(ACC_NONE);
            presenterPlayer.setSteer(STEER_NONE);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            presenterPlayer.setBullet(BULLET_EXIST);
        } else {
            presenterPlayer.setBullet(BULLET_NONE);
        }
    }

    @Override
    public void initTankTower() {
        tankTurret = new PlayerTankTurret(new Vector2(1, 2f), this, world, new Vector2(0, 1f));
    }
}
