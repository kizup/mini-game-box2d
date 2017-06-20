package ru.kizup.minibox2dgame.states;

import static ru.kizup.minibox2dgame.handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.handlers.B2DVars;
import ru.kizup.minibox2dgame.handlers.GameStateManager;
import ru.kizup.minibox2dgame.handlers.MyContactListener;

/**
 * Created by padmitriy on 19.06.17.
 */

public class Play extends GameState {


    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;

    private OrthographicCamera b2dCam;

    public Play(GameStateManager gameStateManager) {
        super(gameStateManager);

        world = new World(new Vector2(0, -9.8f), true);
        world.setContactListener(new MyContactListener());
        box2DDebugRenderer = new Box2DDebugRenderer();

        //create static body
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(MiniGame.V_WIDTH / 2 / PPM, MiniGame.V_HEIGHT / 2 / PPM);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(90 / PPM, 5 / PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_GROUND;
        fixtureDef.filter.maskBits = B2DVars.BIT_BOX | B2DVars.BIT_BALL;
        body.createFixture(fixtureDef).setUserData("border");

        //create dynamic rectangle
        bodyDef.position.set(MiniGame.V_WIDTH / 2 / PPM, MiniGame.V_HEIGHT / 2 / PPM + 10);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        polygonShape.setAsBox(10 / PPM, 20 / PPM);
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_BOX;
        fixtureDef.filter.maskBits = B2DVars.BIT_GROUND;
        fixtureDef.restitution = 0.2f;
        body.createFixture(fixtureDef).setUserData("box");

        //create ball
        bodyDef.position.set(MiniGame.V_WIDTH / PPM / 2 + 1.1f, MiniGame.V_HEIGHT / PPM / 2 + 20);
        body = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(5 / PPM);
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_BALL;
        fixtureDef.filter.maskBits = B2DVars.BIT_GROUND;
        body.createFixture(fixtureDef).setUserData("ball");

        //set up box2d camera
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, MiniGame.V_WIDTH / PPM, MiniGame.V_HEIGHT / PPM);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

        world.step(dt, 6, 2); //accuracy < better


    }

    @Override
    public void render() {
        //clear screen
        Gdx.gl20.glClearColor(0.2f, 0.3f, 0.2f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        box2DDebugRenderer.render(world, b2dCam.combined);
    }

    @Override
    public void dispose() {

    }
}
