package ru.kizup.minibox2dgame.model.factory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.model.tank.EnemyTank;
import ru.kizup.minibox2dgame.model.tank.PlayerTank;
import ru.kizup.minibox2dgame.model.tank.Tank;

/**
 * Created by dmitry on 27.06.2017.
 */

public final class TankFactory extends BaseFactory {

    private float tankWidth;
    private float tankLength;
    private Vector2 position;
    private float angle;
    private float power;
    private ru.kizup.minibox2dgame.model.tank.Tank tank;

    public TankFactory(boolean enemy) {
        if (enemy) tank = new EnemyTank();
        else tank = new PlayerTank();
    }

    public TankFactory width(float width) {
        this.tankWidth = width;
        return this;
    }

    public TankFactory length(float length) {
        this.tankLength = length;
        return this;
    }

    public TankFactory position(Vector2 position) {
        this.position = position;
        return this;
    }

    public TankFactory power(float power) {
        this.power = power;
        return this;
    }

    public Tank build(World world) {
        tank.setWidth(tankWidth);
        tank.setLength(tankLength);
        tank.setPosition(position);
        tank.setPower(power);
        tank.setWorld(world);

        return tank;
    }

}
