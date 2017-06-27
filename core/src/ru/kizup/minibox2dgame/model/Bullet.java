package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.util.BodyEditorLoader;

/**
 * Created by Дмитрий on 22.06.2017.
 */

public class Bullet {

    private Body body;
    private ru.kizup.minibox2dgame.model.turret.TankTurret turret;
    private World world;
    private OptionsBullet optionsBullet;

    private boolean isDestroy = false;

    public Bullet(final World world, ru.kizup.minibox2dgame.model.turret.TankTurret turret, OptionsBullet optionsBullet) {
        this.turret = turret;
        this.world = world;
        this.optionsBullet = optionsBullet;

        initBullet();
    }

    private void initBullet() {
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal(optionsBullet.getPathTexture()));

        Vector2 localPoint = new Vector2(turret.getWidthTurret() - turret.getWidthTurret() / 4,
                turret.getHeightTurret() / 11);

        final BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(turret.getTurretBody().getWorldPoint(localPoint));
        bodyDef.bullet = true;
        bodyDef.angle = (float) (turret.getTurretBody().getAngle() + Math.toRadians(-90));
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = 0f;
        fixtureDef.filter.maskBits = ru.kizup.minibox2dgame.controller.CollisionCategory.MASK_BULLET;
        fixtureDef.filter.categoryBits = ru.kizup.minibox2dgame.controller.CollisionCategory.CATEGORY_BULLET;

        // YBR-365P YBR-365 YBR-365K
        loader.attachFixture(body, optionsBullet.getNameTexture(), fixtureDef, optionsBullet.getScale());
        body.setUserData(this);

        float rot = turret.getTurretBody().getTransform().getRotation();
        float x = MathUtils.cos(rot);
        float y = MathUtils.sin(rot);

        body.setLinearVelocity(5000000f * x, 5000000f * y);
    }

    public void update(float delta) {

    }

    public void destroy() {
        body.setUserData(null);
        isDestroy = true;
    }

    public boolean isDestroy() {
        return isDestroy;
    }

    public Body getBody() {
        return body;
    }

    public int getDamage() {
        return optionsBullet.getDamage();
    }
}
