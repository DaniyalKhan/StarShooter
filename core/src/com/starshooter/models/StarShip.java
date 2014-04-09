package com.starshooter.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.starshooter.util.AlgebraUtils;
import com.starshooter.util.SpriteUtils;

public abstract class StarShip extends Sprite {
		
	private static final Rectangle tmpr = new Rectangle();
	
	public static final String BLUE = "blue";
	public static final String RED = "red";
	public static final String GREEN = "green";
	
	protected static final String GUN = "gun";
	protected static final int NUMBER_GUNS = 10;
	
	protected static final Pool<Laser> laserPool = new Pool<Laser>() {
		@Override
		protected Laser newObject() {
			return new Laser();
		}
	};
	
	//TODO use this for freeing lasers
	public static void freeLaser(Laser laser) {
		laserPool.free(laser);
	}

	public interface LaserListener {
		void onFire(Laser laser);
	}
	
	public interface UIListener {
		void onScoreChange(int addition);
	}
	
	protected int health;
	protected static final float modulationSpeed = 0.1f;
	protected static final Color HIT_COLOUR = new Color(1f, 0f, 0f, 1f);
	protected static final Color DEFAULT_MODULATION = new Color(1f, 1f, 1f, 1f);
	protected final Color modulation;
	
	private final LaserListener listener;
	private final UIListener uiListener;
	
	protected Circle hitBox;
	
	public StarShip(TextureRegion textureRegion, LaserListener listener, UIListener uiListener) {
		super(textureRegion);
		this.health = getMaxHealth();
		Vector2 mid = SpriteUtils.getMid(this);
		this.hitBox = new Circle(mid.x, mid.y, Math.min(getRegionWidth(), getRegionHeight())/2f);
		this.listener = listener;
		this.modulation = DEFAULT_MODULATION;
		this.uiListener = uiListener;
		setColor(modulation);
	}
	
	protected void update(float delta) {
		float r = getColor().r +  modulationSpeed * (DEFAULT_MODULATION.r  - getColor().r);
		float g = getColor().g +  modulationSpeed * (DEFAULT_MODULATION.g - getColor().g);
		float b = getColor().b +  modulationSpeed * (DEFAULT_MODULATION.b - getColor().b);
		float a = getColor().a +  modulationSpeed * (DEFAULT_MODULATION.a - getColor().a);
		setColor(r, g, b, a);
	}
	
	public boolean damage(int damage) {
		health -= damage;
		setColor(HIT_COLOUR);
		return health <= 0;
	}
	
	public boolean checkCollision(Laser laser) {
		tmpr.x = laser.getX();
		tmpr.y = laser.getY();
		tmpr.width = laser.getRegionWidth();
		tmpr.height = laser.getRegionHeight();
		return AlgebraUtils.colliding(tmpr, laser.getRotation(), getHitBox());
	}
	
	public void fire(float laserSpeed, Vector2 position, Vector2 direction, int laserDamage, Laser.Type type) {
		Laser laser = laserPool.obtain();
		String colour = Laser.RED;
		if (type == Laser.Type.Friendly) colour = Laser.BLUE;
		laser.setProperties(position.x, position.y, colour, Laser.TYPE_SHORT, direction.x, direction.y, laserSpeed, type, laserDamage);
		if (listener != null) listener.onFire(laser);
	}
	
	protected void onFire(Laser laser) {
		if (listener != null) listener.onFire(laser);
	}
	

	public Circle getHitBox() {
		return hitBox;
	}

	public int getHealth() {
		return health;
	}
	
	public abstract int getMaxHealth();
	
}
