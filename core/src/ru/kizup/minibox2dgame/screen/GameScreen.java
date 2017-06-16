package ru.kizup.minibox2dgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.model.Assets;

public class GameScreen extends ScreenAdapter {

    private MiniGame game;
    private SpriteBatch batch;
    Texture img;
    Body body;
    World world;


    public GameScreen(MiniGame miniGame) {
        this.game = miniGame;
        this.batch = game.getBatch();
        this.img = Assets.sTexture;

        world = new World(new Vector2(0, -10), true);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = false;
        bodyDef.position.x = 10;
        bodyDef.position.y = 10;

        body = world.createBody(bodyDef);

        MassData massData = new MassData();
        massData.mass = 10;
        massData.center.x = 20;
        massData.center.y = 20;
        body.setMassData(massData);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, body.getPosition().x, body.getPosition().y);
        batch.end();

        world.step(delta, 4, 4);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
