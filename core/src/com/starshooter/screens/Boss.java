package com.starshooter.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.starshooter.models.EnemyShip;
import com.starshooter.models.Laser;
import com.starshooter.models.Laser.Type;
import com.starshooter.models.StarShip;
import com.starshooter.util.AlgebraUtils;
import com.starshooter.util.SpriteUtils;

public class Boss extends StarShip {

	private static final Random RAND = new Random();
	
	private static final Vector2 tmp = new Vector2();
	private static final Vector2 tmp2 = new Vector2();
	private static final Vector2 NORMAL = new Vector2(0, -1);

	private Vector2 lookAt = new Vector2();
	private float dist;
	public int maxHealth;
	private Bezier<Vector2> path;
	float t = 0;
	
	int rotDir = 1;
	
	enum FightMode {Stand, Fight};
	enum Attack {Wave, Shoot};
	
	float lastWaveTime;
	
	static float standSpeed = 20;
	
	FightMode currentMode = FightMode.Stand;
	Attack currentAttack;
	float currentSpeed = standSpeed;
	float bigRotation = 90;
	
	float lastFireTime = 10000f;
	float lastChangeTime = 0;
	float lastRotDirTime = 0;
	
	float total;
	
	public Boss(TextureRegion textureRegion, LaserListener listener, UIListener uiListener, int maxHealth) {
		super(textureRegion, listener, uiListener);
		this.health = maxHealth;
		Vector2[] points = new Vector2[2];
		Vector2 mid = SpriteUtils.getMid(this);
		points[0] = new Vector2(Gdx.graphics.getWidth()/2f - mid.x, Gdx.graphics.getHeight() * 1.2f - mid.y);
		points[1] = new Vector2(points[0].x, points[0].y - 250);
		this.path = new Bezier<Vector2>(points);	
		this.lookAt = new Vector2(Gdx.graphics.getWidth()/2f - getRegionWidth()/2f, Gdx.graphics.getHeight()/2f - getRegionHeight()/2f);
		Vector2 dist = new Vector2(lookAt.x - points[1].x, lookAt.y - points[1].y);
		this.dist = dist.len();
		this.maxHealth = maxHealth;
		this.hitBox = new Circle(0, 0, 50);
	}

	
	
	protected void update(float delta, float xPos, float yPos) {
		super.update(delta);
		if (health < 0) return;
		total += delta;
		t += delta * 0.5f;
		Vector2 p = new Vector2();
		if (t < 0) t = 0;
		if (t >=1) { 
			t = 1;
			tmp.set(MathUtils.cosDeg(bigRotation) * (dist + 280), MathUtils.sinDeg(bigRotation) * dist);
			p.set(tmp.x + lookAt.x, tmp.y + lookAt.y);
			
			if (lastRotDirTime >= 6f && RAND.nextFloat() < 0.1f) {
				rotDir *= -1;
				lastRotDirTime = 0;
			}
			if (currentAttack == null || currentAttack != Attack.Shoot) {
				bigRotation += delta * currentSpeed * rotDir;
			}
		} else {
			path.valueAt(p, t);
		}
		if (currentAttack == null || currentAttack != Attack.Shoot) {
			setPosition(p.x, p.y);
			tmp.set(lookAt.x, lookAt.y).sub(tmp2.set(getX(), getY()));
			setRotation(AlgebraUtils.angle(tmp, NORMAL));
		}
		
		lastFireTime += delta;
		lastChangeTime +=delta;
		lastRotDirTime +=delta;
		
		if (currentMode == FightMode.Stand) {
			currentAttack = null;
			if (lastFireTime >= 0.2f) {
				for (int i = 0; i < 20; i ++) {
					fire(300, SpriteUtils.getMid(this), tmp.set(MathUtils.cosDeg(i * 360f/20f), MathUtils.sinDeg(i * 360f/20f)), 1, Laser.Type.Foe);
				}
				lastFireTime = 0;
			}
			if (t >= 1 && lastChangeTime >= 1f) {
				float r = RAND.nextFloat();
				if (r <= 0.33f) currentMode = FightMode.Fight;
				lastChangeTime = 0;
			}
		} 
		if (currentMode == FightMode.Fight && lastChangeTime > 3f) {
			float r = RAND.nextFloat();
			if (r <= 0.33f && currentAttack != Attack.Wave) {
				currentAttack = Attack.Wave;
				lastWaveTime = 0;
				currentSpeed =  50f;
			} else if (r <= 0.66f) {
				currentAttack = Attack.Shoot;
				currentSpeed =  100f;
			} else {
				currentMode = FightMode.Stand;
				currentSpeed =  standSpeed;
			}
			lastChangeTime = 0;
		}
		
		if (currentMode == FightMode.Fight && currentAttack == Attack.Wave) {
			if (lastWaveTime >= 0.03f) {
				for (int i = -1; i < 2; i++) {
					float angle = (float) (getRotation() + MathUtils.cos(total) * 90 + i * 20);
					fire(900, SpriteUtils.getMid(this), tmp.set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)), 1, Laser.Type.Foe);
				}
				lastWaveTime = 0;
			}
			lastWaveTime += delta;
		}
		
		if (currentMode == FightMode.Fight && currentAttack == Attack.Shoot) {
			if (lastWaveTime >= 0.2f) {
				tmp.set(xPos, yPos).sub(tmp2.set(getX(), getY())).rotate(90).rotate(5);
				fire(1200, SpriteUtils.getMid(this), tmp, 3, Laser.Type.Foe);
				tmp.set(xPos, yPos).sub(tmp2.set(getX(), getY())).rotate(90).rotate(-5);
				fire(1200, SpriteUtils.getMid(this), tmp, 3, Laser.Type.Foe);
				lastWaveTime = 0;
			}
			lastWaveTime += delta;
		}
		
	}



	@Override
	public Circle getHitBox() {
		Vector2 tmp = SpriteUtils.getMid(this);
		hitBox.x = tmp.x;
		hitBox.y = tmp.y;
		return super.getHitBox();
	}

	
	public void debug(ShapeRenderer s) {
		s.circle(getHitBox().x, getHitBox().y, getHitBox().radius);
	}


	@Override
	public boolean checkCollision(Laser laser) {
		if (health <0) return false;
		boolean hit = super.checkCollision(laser);
		if (hit) {
			if (damage(laser.getDamageDealt())) {
				modulation = EnemyShip.FADE;
			}
		}
		return hit;
	}



	@Override
	public int getMaxHealth() {
		return maxHealth;
	}
}
