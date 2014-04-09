package com.starshooter.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SpriteUtils {

	public static Vector2 tmp = new Vector2();
	
	public static Vector2 getMid(Sprite s) {
		return tmp.set(s.getX() + s.getRegionWidth()/2f, s.getY() + s.getRegionHeight()/2f);
	}
	
	public static Vector2 getMid(Rectangle r) {
		return tmp.set(r.x + r.width/2f, r.y + r.height/2f);
	}

	public static void center(Sprite s, float x, float y) {
		s.setPosition(x, y);
		s.translate(-s.getRegionWidth()/2f, -s.getRegionHeight()/2f);
	}
	
	public static void customDraw(Batch batch, Sprite s, float rotation) {
		//FIXME: weird hack??
		float width = s.getRegionHeight();
		float height = s.getRegionWidth();
		float originX = width/2f;
		float originY = height/2f;
		batch.draw(s, s.getX() - originX, s.getY() - originY, originX, originY, width, height, 1f, 1f, rotation, true);
		
	}
	
	public static void customDraw(Batch batch, TextureRegion t, float rotation, float x, float y) {
		//FIXME: weird hack??
		float width = t.getRegionHeight();
		float height = t.getRegionWidth();
		float originX = width/2f;
		float originY = height/2f;
		batch.draw(t, x - originX, y - originY, originX, originY, width, height, 1f, 1f, rotation, true);
		
	}
	
}
