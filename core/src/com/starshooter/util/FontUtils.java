package com.starshooter.util;

import java.text.DecimalFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FontUtils {
	
	private static final BitmapFont FONT_UI = new BitmapFont(Gdx.files.internal("fonts/kenvector.fnt"));
	
	public static void drawShadedFont(SpriteBatch batch, float percentage, String str, float x, float y) {
		Color norm = FONT_UI.getColor();
		FONT_UI.setColor(clamp(1 - percentage), clamp(percentage), 0, 1);
		FONT_UI.draw(batch, str, x, y);
		FONT_UI.setColor(norm);
	}
	
	private static float clamp(float f) {
		if (f <= 0.33f) return 0.33f;
		if (f >= 0.66f) return 0.66f;
		return f;
	}
	
	public static float getWidth(String s) {
		return FONT_UI.getBounds(s).width;
	}
	
	public static void draw(SpriteBatch batch, String str, float x, float y) {
		FONT_UI.setColor(1f, 1f, 1f, 1f);
		FONT_UI.draw(batch, str, x, y);
	}
	
	public static void drawBlack(SpriteBatch batch, String str, float x, float y) {
		FONT_UI.setColor(0f, 0f, 0f, 1f);
		FONT_UI.draw(batch, str, x, y);
	}


}
