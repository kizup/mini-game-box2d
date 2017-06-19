package ru.kizup.minibox2dgame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

import ru.kizup.minibox2dgame.MiniGame;
import ru.kizup.minibox2dgame.model.Assets;

public class GameScreen extends ScreenAdapter {

    private MiniGame miniGame;
    private SpriteBatch sb;
    public Texture img;
    public Body body;
    public World world;


    public GameScreen(MiniGame miniGame) {
        this.miniGame = miniGame;
        this.sb = miniGame.getSpriteBatch();
        this.img = Assets.sTexture;

        world = new World(new Vector2(0, 0), true);

        BodyDef adsBody = new BodyDef();
        adsBody.type = BodyDef.BodyType.DynamicBody;
        adsBody.bullet = false;
        adsBody.position.x = 150;
        adsBody.position.y = 50;

        body = world.createBody(adsBody);

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
        sb.setProjectionMatrix(miniGame.getCamera().combined);
        sb.begin();
        sb.draw(img, body.getPosition().x, body.getPosition().y);
        sb.end();

        world.step(delta, 4, 4);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
