package com.starshooter.models;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.starshooter.util.AlgebraUtils;
import com.starshooter.util.SpriteUtils;
import com.starshooter.util.TextureCache;

public class Laser extends Sprite implements Poolable {

	private static final Vector2 NORMAL = new Vector2(0, 1);
	private static final float rotationFromTerminal = AlgebraUtils.angleFromTermninal(NORMAL);
	
	public static enum Type {Friendly, Foe};
	public static String TYPE_LONG = "01";
	public static String TYPE_MEDIUM = "05";
	public static String TYPE_SHORT = "07";
	public static String LASER = "laser";
	public static String RED = "Red";
	public static String BLUE = "Blue";
	public static String GREEN = "Green";
	
	private Vector2 direction = new Vector2();
	private float speed; 
	private Type type; 
	private int damage;

	public void setProperties(float x, float y, String colour, String type, float xDirection, float yDirection, float speed, Type friendly, int damage) {
		setRegion(TextureCache.obtain().get(LASER + colour + type));
		this.direction.set(xDirection, yDirection);
		this.speed = speed;
		this.type = friendly;
		this.damage = damage;
		setOrigin(getWidth()/2f, getHeight()/2f);
		setBounds(x - getWidth()/2f, y - getHeight()/2f, getRegionWidth(), getRegionHeight());
		setRotation(AlgebraUtils.angle(direction, NORMAL));
	}
	
	public int getDamageDealt() {
		return damage; 
	}
	
	public boolean isFriendly() {
		return type == Type.Friendly;
	}

	public void update(float delta) {
		float dx = speed * delta * MathUtils.cosDeg(getRotation());
		float dy = speed * delta * MathUtils.sinDeg(getRotation());
		translate(dx, dy);
	}

	@Override
	public void reset() {
		
	}
	
	@Override
	public void draw(Batch batch) {
		SpriteUtils.customDraw(batch, this, getRotation());
	}
	
	
}
