package ru.kizup.minibox2dgame.model.tank;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_EXIST;
import static ru.kizup.minibox2dgame.screen.GameScreen.BULLET_NONE;

/**
 * Created by Neuron on 23.06.2017.
 */

public class PlayerTank extends Tank {

    public PlayerTank() {}

    public PlayerTank(float width, float length, Vector2 position, float angle, float power, float maxSteerAngle, float maxSpeed, World world) {
        super(width, length, position, angle, power, maxSteerAngle, maxSpeed, world, 4);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            accelerate = ACC_ACCELERATE;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            accelerate = ACC_BRAKE;
        } else {
            accelerate = ACC_NONE;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            steer = STEER_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            steer = STEER_RIGHT;
        } else {
            steer = STEER_NONE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            accelerate = ACC_NONE;
            steer = STEER_NONE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)) {
            bullet = BULLET_EXIST;
        } else {
            bullet = BULLET_NONE;
        }
    }

    @Override
    public boolean isEnemy() {
        return false;
    }
}
