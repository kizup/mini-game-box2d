package ru.kizup.minibox2dgame.model.newtank.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.model.newtank.TankContact;
import ru.kizup.minibox2dgame.model.newtank.TankView;

import static ru.kizup.minibox2dgame.model.newtank.Tank.*;
import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_EXIST;
import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_NONE;

/**
 * Created by yks-11 on 11/21/17.
 */

public class TankPlayerView extends TankView implements TankPlayerContact.View{

    private TankPlayerContact.Presenter presenter;

    public TankPlayerView(TankPlayerContact.Presenter presenter, World world, SteeringBehavior<Vector2> behavior) {
        super(presenter, world, behavior);
        this.presenter = presenter;

        presenter.setView(this);
    }

    @Override
    public void update(float delta, int hitPoints, int accelerate, float maxSpeed, int steer, float power, float koefRotation, long shootTime, long cooldownTime, int bullet) {
        super.update(delta, hitPoints, accelerate, maxSpeed, steer, power, koefRotation, shootTime, cooldownTime, bullet);

    }

    @Override
    public void handleInput(int accelerate, int steer, int bullet) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            presenter.setAccelerate(ACC_ACCELERATE);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            presenter.setAccelerate(ACC_BRAKE);
        } else {
            presenter.setAccelerate(ACC_NONE);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            presenter.setSteer(STEER_LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            presenter.setSteer(STEER_RIGHT);
        } else {
            presenter.setSteer(STEER_NONE);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            presenter.setAccelerate(ACC_NONE);
            presenter.setSteer(STEER_NONE);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            presenter.setBullet(BULLET_EXIST);
        } else {
            presenter.setBullet(BULLET_NONE);
        }
    }
}
