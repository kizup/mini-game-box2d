package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Дмитрий on 22.06.2017.
 */

public class BoxProp {

    private float width;
    private float length;
    private float x;
    private float y;
    private Body boxBody;

    public BoxProp(float width, float length, float x, float y, World world) {
        this.width = width;
        this.length = length;
        this.x = x;
        this.y = y;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x, y));
        bodyDef.angle = 0;
        bodyDef.fixedRotation = true;

        boxBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, length / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.4f;
        boxBody.createFixture(fixtureDef);
    }

}
