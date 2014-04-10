package com.starshooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.starshooter.StarShooter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1440;
		config.height = 900;
		config.useGL30 = true;
		config.fullscreen = false;
		new LwjglApplication(new StarShooter(), config);
	}
}
