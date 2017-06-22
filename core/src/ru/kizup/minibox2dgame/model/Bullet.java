package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Дмитрий on 22.06.2017.
 */

public class Bullet implements ContactFilter {

    private float x;
    private float y;
    private Body body;
    private TankTurret turret;

    public Bullet(float x, float y, final World world, TankTurret turret, Vehicle vehicle) {
        this.x = x;
        this.y = y;
        this.turret = turret;

        final BodyDef bodyDef = new BodyDef();
//        bodyDef.position.set(turret.getTurretBody().getPosition());
        bodyDef.position.set(vehicle.getBody().getPosition().x, vehicle.getBody().getPosition().y);
        bodyDef.bullet = true;
//        bodyDef.angle = turret.getTurretBody().getAngle();
        bodyDef.angle = vehicle.getBody().getAngle();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);

//        body.applyForce((float) (1500 * Math.sin(vehicle.getBody().getAngle())), (float) (1500 * -Math.cos(vehicle.getBody().getAngle())), ((Tank) vehicle).getPositionX(), ((Tank) vehicle).getPositionX(), true);
        body.setLinearVelocity((float) (100 * Math.sin(vehicle.getBody().getAngle())), (float) (100 * -Math.cos(vehicle.getBody().getAngle())));
    }

    public void update(float delta) {

    }

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        return false;
    }
}
