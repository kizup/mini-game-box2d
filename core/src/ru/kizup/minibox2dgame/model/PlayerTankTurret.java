package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_LEFT;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_NONE;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_RIGHT;

/**
 * Created by Neuron on 23.06.2017.
 */

public class PlayerTankTurret extends TankTurret{

    PlayerTankTurret(Vector2 position, Vehicle vehicle, World world, Vector2 margin) {
        super(position, vehicle, world, margin);
    }

    @Override
    public void update() {
        super.update();

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            steer = STEER_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            steer = STEER_RIGHT;
        } else {
            steer = STEER_NONE;
        }

        float tankRotation = vehicle.getBody().getAngle() - tankPrevRotation;

        switch (steer) {
            case STEER_RIGHT:{
                bodyTurret.setTransform(bodyTurret.getPosition(), bodyTurret.getAngle() - SPEED_ROTATION);
                break;
            }
            case STEER_LEFT: {
                bodyTurret.setTransform(bodyTurret.getPosition(), bodyTurret.getAngle() + SPEED_ROTATION);
                break;
            }
            case STEER_NONE: {
                bodyTurret.setTransform(bodyTurret.getPosition(), bodyTurret.getAngle() + tankRotation);
                break;
            }
        }

        tankPrevRotation = vehicle.getBody().getAngle();
    }
}
