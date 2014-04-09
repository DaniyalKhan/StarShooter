package com.starshooter;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.starshooter.util.TextureCache;

public class StarField {
	
	private static final String BG_BLUE = StarShooter.DIR_BACKGROUNDS + "blue.png";
	private static final String BG_BLACK = StarShooter.DIR_BACKGROUNDS + "black.png";
	private static final String BG_DARKPURPLE = StarShooter.DIR_BACKGROUNDS + "darkPurple.png";
	private static final String BG_PURPLE = StarShooter.DIR_BACKGROUNDS + "purple.png";
	
	private static final String STAR = "star";
	private static final String METEOR = "meteorBrown_tiny";
	
	private float fieldWidth;
	private float fieldHeight;
	private float y = 0;
	private Vector2 scrollSpeed = new Vector2(0, 700);
	private TextureRegion background;
	
	private final Pool<Sprite> celestialPool = new Pool<Sprite>() {
		@Override
		protected Sprite newObject() {
//			if (rand.nextInt(7) == 0) {
//				int size = rand.nextInt(1) + 1;
//				return new Sprite(TextureCache.obtain().get(METEOR + size));
//			} else {
				int size = rand.nextInt(2) + 1;
				return new Sprite(TextureCache.obtain().get(STAR + size));
//			}
		}
	};
	private final Array<Sprite> celestialBodies;
	private final Random rand = new Random();
	
	public StarField(float fieldWidth, float fieldHeight) {
		this.fieldWidth = fieldWidth;
		this.fieldHeight = fieldHeight;
		this.background = new TextureRegion(new Texture(BG_BLACK));
		this.celestialBodies = new Array<Sprite>(false, 24);
	}
	
	public void advance(float deltaTime) {
		y -= Math.round(scrollSpeed.y * deltaTime);
		Iterator<Sprite> it = celestialBodies.iterator();
		while (it.hasNext()) {
			Sprite body = it.next();
			body.translateY(-Math.round(scrollSpeed.y * deltaTime));
			if (body.getY() + body.getHeight() < 0) {
				it.remove();
				celestialPool.free(body);
			}
		}
		if (y < -background.getRegionHeight()) {
			y += background.getRegionHeight();
			spawn();
			spawn();
		}
	}
	
	private void spawn() {
		Sprite body = celestialPool.obtain();
		body.setX(rand.nextFloat() * fieldWidth);
		body.setY(rand.nextFloat() * background.getRegionHeight() + fieldHeight);
		celestialBodies.add(body);
	}

	public void render(SpriteBatch batch) {
		int i = 0;
		while (i < fieldWidth) {
			float j = y;
			while (j < fieldHeight) {
				batch.draw(background, i, j);
				j += background.getRegionHeight();
			}
			i += background.getRegionWidth();
		}
		for (Sprite body: celestialBodies) body.draw(batch);
	}

}
