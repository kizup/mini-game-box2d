package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.util.Util;

import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_LEFT;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_NONE;
import static ru.kizup.minibox2dgame.screen.GameScreen.STEER_RIGHT;

/**
 * Created by Neuron on 23.06.2017.
 */

public class PlayerTankTurret extends TankTurret{

    PlayerTankTurret(Vector2 position, Vehicle vehicle, World world) {
        super(position, vehicle, world);
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
                turret.setTransform(turret.getPosition(), turret.getAngle() - SPEED_ROTATION);
                break;
            }
            case STEER_LEFT: {
                turret.setTransform(turret.getPosition(), turret.getAngle() + SPEED_ROTATION);
                break;
            }
            case STEER_NONE: {
                turret.setTransform(turret.getPosition(), turret.getAngle() + tankRotation);
                break;
            }
        }

        tankPrevRotation = vehicle.getBody().getAngle();
    }
}
