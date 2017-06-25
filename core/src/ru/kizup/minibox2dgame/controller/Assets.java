package ru.kizup.minibox2dgame.controller;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by dpuzikov on 16.06.17.
 */

public final class Assets {

    public static Texture sTexture;

    static {
        sTexture = new Texture("badlogic.jpg");
    }

    public static void dispose() {
        sTexture.dispose();
    }

}
