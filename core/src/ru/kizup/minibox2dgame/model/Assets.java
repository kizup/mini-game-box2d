package ru.kizup.minibox2dgame.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

/**
 * Created by dpuzikov on 16.06.17.
 */

public final class Assets {

    public static Texture sTexture;
    public static ParticleEffect sSmallExplosionEffect;
    public static ParticleEffect sExplosionEffect;

    static {
        sTexture = new Texture("badlogic.jpg");
        sExplosionEffect = new ParticleEffect();
        sExplosionEffect.load(Gdx.files.internal("particles/explosion.p"), Gdx.files.internal("particles/"));

        sSmallExplosionEffect = new ParticleEffect();
        sSmallExplosionEffect.load(Gdx.files.internal("particles/explosion_small.p"), Gdx.files.internal("particles/"));
    }

    public static void dispose() {
        sTexture.dispose();
        sExplosionEffect.dispose();
        sSmallExplosionEffect.dispose();
    }

}
