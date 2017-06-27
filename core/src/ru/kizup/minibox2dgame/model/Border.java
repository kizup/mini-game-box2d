package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.controller.CollisionCategory;

/**
 * Created by dmitry on 27.06.2017.
 */

public class Border {

    public Border(float width, float length, float x, float y, World world, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.angle = 0;
        bodyDef.fixedRotation = true;

        Body boxBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, length / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.4f;
        fixtureDef.filter.maskBits = maskBits;
        fixtureDef.filter.categoryBits = CollisionCategory.CATEGORY_SCENERY;

        boxBody.createFixture(fixtureDef);
        boxBody.setUserData(this);
    }

}
