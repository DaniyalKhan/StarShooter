package com.starshooter.models;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.starshooter.util.SpriteUtils;
import com.starshooter.util.TextureCache;

public class EnemyShip extends StarShip {

	private static final String ENEMY = "enemy";
	public static final String BLACK = "Black";
	
	//note to self: type 1 was weird hit box
	public enum EnemyType {TYPE_1, TYPE_2, TYPE_3, TYPE_4, TYEPE_5};
	
	private final EnemyType type;
	private final Circle hitBox;
	
	public EnemyShip(String color, EnemyType type, LaserListener listener) {
		super(TextureCache.obtain().get(ENEMY + color + (type.ordinal() + 1)), listener);
		this.type = type;
		Vector2 mid = SpriteUtils.getMid(this);
		this.hitBox = new Circle(mid.x, mid.y, Math.min(getRegionWidth(), getRegionHeight())/2f);
		this.health = (type.ordinal() + 1) * 2;
	}
	
	public Circle getHitBox() {
		return hitBox;
	}

	@Override
	protected int getMaxHealth() {
		return 0;
	}

}
