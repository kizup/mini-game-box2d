package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.util.BodyEditorLoader;

import static com.badlogic.gdx.math.Rectangle.tmp;

/**
 * Created by Дмитрий on 22.06.2017.
 */

public class Bullet implements ContactFilter {

    private Body body;
    private TankTurret turret;
    private World world;

    private final float BOTTLE_WIDTH = 20f;

    public Bullet(final World world, TankTurret turret) {
        this.turret = turret;
        this.world = world;

        initBullet();
//        turret.getTurretBody().getFixtureList().get(0).getShape().getRadius()
//        body.applyForce((float) (1 * Math.sin(vehicle.getBody().getAngle())), (float) (1500 * -Math.cos(vehicle.getBody().getAngle())), ((Tank) vehicle).getPositionX(), ((Tank) vehicle).getPositionX(), true);
//        body.setLinearVelocity((float) (100 * Math.sin(body.getTurretBody().getAngle() + Math.toRadians(-90))), (float) (100 * Math.cos(turret.getTurretBody().getAngle() + Math.toRadians(-90))));
    }

    private void initBullet(){
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("bullet.json"));

        final BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(turret.getTurretBody().getWorldPoint(new Vector2(turret.widthTurret / 2, turret.heightTurret / 11)));
        bodyDef.bullet = false;
        bodyDef.angle = (float) (turret.getTurretBody().getAngle() + Math.toRadians(-90));
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.restitution = 0f;
        // YBR-365P YBR-365 YBR-365K
        loader.attachFixture(body, "YBR-365P", fixtureDef, BOTTLE_WIDTH);
        body.getFixtureList().get(0).setUserData(this);
        body.createFixture(fixtureDef);

        float rot = turret.getTurretBody().getTransform().getRotation();
        float x = MathUtils.cos(rot);
        float y = MathUtils.sin(rot);

        body.setLinearVelocity(50000 * x, 50000 * y);
    }

    public void update(float delta) {

    }

    @Override
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        System.out.println("BULLET");
        return false;
    }
}
