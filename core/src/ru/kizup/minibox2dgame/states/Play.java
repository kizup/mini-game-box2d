package ru.kizup.minibox2dgame.states;

import static ru.kizup.minibox2dgame.handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.handlers.GameStateManager;

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
        body.createFixture(fixtureDef);

        //create dynamic body
        bodyDef.position.set(MiniGame.V_WIDTH / 2 / PPM, MiniGame.V_HEIGHT / 2 / PPM + 10);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        polygonShape.setAsBox(10 / PPM, 20 / PPM);
        fixtureDef.shape = polygonShape;
        fixtureDef.restitution = 0.2f;
        body.createFixture(fixtureDef);

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
