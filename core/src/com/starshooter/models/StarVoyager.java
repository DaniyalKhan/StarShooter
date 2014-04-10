package com.starshooter.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.starshooter.StarShooter;
import com.starshooter.controllers.PlayStation3;
import com.starshooter.controllers.PlayStation3.PS3ButtonCallback;
import com.starshooter.util.SpriteUtils;
import com.starshooter.util.TextureCache;

public class StarVoyager extends StarShip implements Disposable {
	
	private static final Vector2 cannonTemp = new Vector2();
	private static final Vector2 tmp2 = new Vector2();
	
	private static final String DAMAGE_DIR = "damage/";
	
	private static final String PLAYER = "player";
	private static final String PLAYER_SHIP = "playerShip";
	private static final String SHIP_1 = "1_";
	private static final String SHIP_2 = "2_";
	private static final String SHIP_3 = "3_";
	private static final String LIFE = "Life";
	private static final String DAMAGE = "damage";

	private static final String ORANGE = "orange";
	private static final String YELLOW = "yellow";
	
	private static final Sound laserFire = Gdx.audio.newSound(Gdx.files.internal(StarShooter.DIR_AUDIO + "sfx_laser1.ogg"));
	
	private static final float fireRate = 0.1f; // 1 bullet per <rate> seconds
	private static final float laserSpeed = 1000f;
	
	private static final float cannonRecoveryTime = 0.15f; // 1 % per <recovery> seconds
	
	private static enum Cannons {Front, Left, Right, Rear};
	
	public abstract class CannonMode {
		
		protected float cost;
		protected int laserDamage;
		protected final StarVoyager voyager;
		
		public CannonMode(float cost, int laserDamage, StarVoyager voyager) {
			this.cost = cost;
			this.laserDamage = laserDamage;
			this.voyager = voyager;
		}
		
		public void fire(float laserSpeed, Vector2 position, Vector2 direction) {
			cannonPower -= cost;
			if (!firedThisFrame) {
				laserFire.play();
				firedThisFrame = true;
			}
		}

	}
	
	private CannonMode burst = new CannonMode(0.5f, 1, this) {
		@Override
		public void fire(float laserSpeed, Vector2 position, Vector2 direction) {
			if (voyager.cannonPower < cost) return;
			super.fire(laserSpeed, position, direction);
			voyager.fire(laserSpeed, position, direction, laserDamage, Laser.Type.Friendly);
			voyager.fire(laserSpeed, position, direction.rotate(-30), laserDamage, Laser.Type.Friendly);
			voyager.fire(laserSpeed, position, direction.rotate(60), laserDamage, Laser.Type.Friendly);
		}
	};
	
	private CannonMode stream = new CannonMode(0.3f, 2, this) {
		@Override
		public void fire(float laserSpeed, Vector2 position, Vector2 direction) {
			if (voyager.cannonPower < cost) return;
			super.fire(laserSpeed, position, direction);
			voyager.fire(laserSpeed, position, direction, laserDamage, Laser.Type.Friendly);
		}
	};
	
	public int cannonModeIndex = 0;
	private CannonMode[] modes = new CannonMode[] {stream, burst};
	public String[] modeNames = new String[] {"STREAM", "BURST"}; 
	
	private static final float SIZE = 40;
	
	private final PlayStation3 controller;
	
	private boolean frontCannon = true;
	private boolean leftCannon = false;
	private boolean rightCannon = false;
	private boolean rearCannon = false;
	
	private float lastRecoveryTime = cannonRecoveryTime;
	private float lastFireTime = fireRate;
	private boolean firedThisFrame = false;
	
	//UI STUFF
	public final Sprite lifeIcon;
	public final Sprite fireModeIcon;
	public int numLives = 3;
	public float cannonPower = 100;
	
	private String shipType = SHIP_1;
	private String colour = ORANGE;
	
	public StarVoyager(LaserListener listener, UIListener uiListener) {
		super(TextureCache.obtain().get(PLAYER_SHIP + SHIP_1 + ORANGE), listener, uiListener);
		controller = new PlayStation3(new PS3ButtonCallback() {
			@Override
			public void onTriangle() {frontCannon = !frontCannon;}
			@Override
			public void onStart() {}
			@Override
			public void onSquare() {leftCannon = !leftCannon;}
			@Override
			public void onCross() {rearCannon = !rearCannon;}
			@Override
			public void onCircle() {rightCannon = !rightCannon;}
			@Override
			public void onL1() {
				cannonModeIndex = cannonModeIndex == 0 ? modes.length - 1 : cannonModeIndex - 1;
			}
			@Override
			public void onR1() {
				cannonModeIndex = (cannonModeIndex + 1) % modes.length;
			}
		});
		setX(Gdx.graphics.getWidth()/2f - getWidth()/2f);
		setY(Gdx.graphics.getHeight()/2f - getHeight()/2f);
		lifeIcon = new Sprite(TextureCache.obtain().get(PLAYER + LIFE + SHIP_1 + ORANGE));
		if (colour.equals(ORANGE)) colour = YELLOW;
		fireModeIcon = new Sprite(TextureCache.obtain().get("button" + colour.substring(0, 1).toUpperCase() + colour.substring(1)));
		hitBox.radius = SIZE;
	}

	private Vector2 cannonPosition(Cannons cannon) {
		if (cannon == Cannons.Front) return cannonTemp.set(getX() + getWidth()/2f,  getY() + getHeight());
		else if (cannon == Cannons.Left) return cannonTemp.set(getX(),  getY() + getHeight()/3f);
		else if (cannon == Cannons.Right) return cannonTemp.set(getX() + getWidth(),  getY() + getHeight()/3f);
		else return cannonTemp.set(getX() + getWidth()/2f,  getY());
	}

	@Override
	public int getMaxHealth() {
		return 30;
	}

	public void update(float delta) {
		super.update(delta);
		float cx = controller.pollLeftAxisX();
		float cy = controller.pollLeftAxisY();
		translate(600 * cx * delta, 400 * cy * delta);
		lastFireTime += delta;
		lastRecoveryTime += delta;
		if (lastRecoveryTime >= cannonRecoveryTime) {
			lastRecoveryTime = 0;
			cannonPower++;
			if (cannonPower > 100) cannonPower = 100;
		}
		firedThisFrame = false;
		if (controller.pollR2() && lastFireTime >= fireRate) {
			lastFireTime = 0;
			if (frontCannon) modes[cannonModeIndex].fire(laserSpeed, cannonPosition(Cannons.Front), tmp2.set(-1, 0));
			if (leftCannon) modes[cannonModeIndex].fire(laserSpeed, cannonPosition(Cannons.Left), tmp2.set(0, -1));
			if (rightCannon) modes[cannonModeIndex].fire(laserSpeed, cannonPosition(Cannons.Right), tmp2.set(0, 1));
			if (rearCannon) modes[cannonModeIndex].fire(laserSpeed, cannonPosition(Cannons.Rear), tmp2.set(1, 0));
		}
		Vector2 mid = SpriteUtils.getMid(this);
		hitBox.x = mid.x;
		hitBox.y = mid.y;
	}

	@Override
	public void draw(Batch batch) {
		drawCannons(batch);
		super.draw(batch);
		drawDamage(batch);
	}
	
	private void drawDamage(Batch batch) {
//		int amountDamaged = 3 - (int) (Math.ceil(health * 1f / getMaxHealth())) * 2;
//		TextureRegion tr = new TextureRegion(new Texture(Gdx.files.internal(StarShooter.DIR_GRAPHICS + DAMAGE_DIR + PLAYER_SHIP + shipType + DAMAGE + amountDamaged + ".png")));
//		batch.draw(tr, getX(), getY());
	}
	
	private void drawCannons(Batch batch) {
		TextureRegion gun = TextureCache.obtain().get(GUN + "06");
		if (frontCannon) drawGun(batch, gun, cannonPosition(Cannons.Front), 270);
		if (leftCannon) drawGun(batch, gun, cannonPosition(Cannons.Left), 0);
		if (rightCannon) drawGun(batch, gun, cannonPosition(Cannons.Right), 180);
		if (rearCannon) drawGun(batch, gun, cannonPosition(Cannons.Rear), 90);
	}
	
	private void drawGun(Batch batch, TextureRegion gun, Vector2 position, float rotation) {
		SpriteUtils.customDraw(batch, gun, rotation, position.x, position.y);
	}
	
	@Override
	public void dispose() {
		laserFire.dispose();
	}

	@Override
	public boolean damage(int damage) {
		boolean dead = super.damage(damage);
		if (dead) {
			numLives--;
			health = getMaxHealth();
		}
		return dead;			
	}
	
	
}
