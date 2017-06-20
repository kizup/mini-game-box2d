package ru.kizup.minibox2dgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.kizup.minibox2dgame.MiniGame;

public class DesktopLauncher {
	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = MiniGame.TITLE;
		config.width = MiniGame.V_WIDTH;
		config.height = MiniGame.V_HEIGHT;

		new LwjglApplication(new MiniGame(), config);
	}
}
