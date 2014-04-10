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
	
	private static final Sound laserFire = Gdx.audio.newSound(Gdx.files.internal(StarShooter.DIR_AUDIO + "sfx_laser1.ogg"));
	
	private static final float fireRate = 0.1f; // 1 bullet per <rate> seconds
	private static final float laserSpeed = 1000f;
	
	private static final float cannonRecoveryTime = 0.15f; // 1 % per <recovery> seconds
	
	private static enum Cannons {Front, Left, Right, Rear};
	
	private static final float cannonCostStream = 0.5f;
	private static final float cannonCostBurst = 0.3f;
	private static final float cannonCostBomb = 2f;
	private float cannonCost;
	
	private static final int cannonDamageStream = 2;
	private static final int cannonDamageBurst = 1;
	private static final int cannonDamageBomb = 8;
	private int cannonDamage = 1;
	
	private static enum FireMode {Stream, Burst, Bomb};
	private FireMode current = FireMode.Stream;
	
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
				int newMode = current.ordinal() == 0 ? FireMode.values().length - 1 : (current.ordinal() - 1) % FireMode.values().length;
				current = FireMode.values()[newMode];}
			@Override
			public void onR1() {current = FireMode.values()[(current.ordinal() + 1) % FireMode.values().length];}
		});
		setX(Gdx.graphics.getWidth()/2f - getWidth()/2f);
		setY(Gdx.graphics.getHeight()/2f - getHeight()/2f);
		lifeIcon = new Sprite(TextureCache.obtain().get(PLAYER + LIFE + SHIP_1 + ORANGE));
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
			if (frontCannon) gaurdedFireMode(laserSpeed, cannonPosition(Cannons.Front), tmp2.set(-1, 0));
			if (leftCannon) gaurdedFireMode(laserSpeed, cannonPosition(Cannons.Left), tmp2.set(0, -1));
			if (rightCannon) gaurdedFireMode(laserSpeed, cannonPosition(Cannons.Right), tmp2.set(0, 1));
			if (rearCannon) gaurdedFireMode(laserSpeed, cannonPosition(Cannons.Rear), tmp2.set(1, 0));
		}
		Vector2 mid = SpriteUtils.getMid(this);
		hitBox.x = mid.x;
		hitBox.y = mid.y;
	}
	
	private void gaurdedFireMode(float laserSpeed, Vector2 position, Vector2 direction) {
		if (current == FireMode.Stream) {
			cannonCost = cannonCostStream;
			cannonDamage = cannonDamageStream;
			fire(laserSpeed, position, direction, cannonDamage, Laser.Type.Friendly);
			fire(laserSpeed, position, direction.rotate(-30), cannonDamage, Laser.Type.Friendly);
			fire(laserSpeed, position, direction.rotate(60), cannonDamage, Laser.Type.Friendly);
		} else if (current == FireMode.Burst){
			cannonCost = cannonCostBurst;
			cannonDamage = cannonDamageBurst;
			fire(laserSpeed, position, direction, cannonDamage, Laser.Type.Friendly);
		}
	}
	
	@Override
	public void fire(float laserSpeed, Vector2 position, Vector2 direction, int damage, Laser.Type type) {
		if (cannonPower < cannonCost) return;
		cannonPower -= cannonCost;
		if (!firedThisFrame) {
			laserFire.play();
			firedThisFrame = true;
		}
		super.fire(laserSpeed, position, direction, damage, type);
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
