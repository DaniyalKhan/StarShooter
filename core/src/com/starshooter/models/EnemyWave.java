package com.starshooter.models;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.starshooter.StarShooter;
import com.starshooter.models.EnemyShip.EnemyType;
import com.starshooter.models.StarShip.LaserListener;
import com.starshooter.models.StarShip.UIListener;
import com.starshooter.util.AlgebraUtils;
import com.starshooter.util.SpriteUtils;

public class EnemyWave {
	
	private static enum Side {Top, Bottom, Left, Right};
	private static Array<Side> sides = new Array<Side>(new Side[]{Side.Top, Side.Bottom, Side.Left, Side.Right});
	
	private static final float MAX_SHIP_SIZE = 100;
	
	private static final Random rand = new Random();
	
	private static int currentSpawnNumber = 3;
	
	private static float spawnThreshold = 2.5f;
	private static float LastSpawnTime = 0;
	private static float totalTimeMin = 0;
	
	private static final Sound deadSound = Gdx.audio.newSound(Gdx.files.internal(StarShooter.DIR_AUDIO + "sfx_zap.ogg"));
	
	public static EnemyWave update(float delta, LaserListener listener, UIListener uiListener, boolean force) {
		if (force) {
			return gen(listener, uiListener);
		}
		totalTimeMin += delta/60f;
		currentSpawnNumber = Math.max(MathUtils.round(totalTimeMin) * 2, 2);
		LastSpawnTime += delta;
		if (LastSpawnTime >= spawnThreshold) {
			LastSpawnTime = 0;
			return gen(listener, uiListener);
		}
		return null;
	}
	
	private static EnemyWave gen(LaserListener listener, UIListener uiListener) {
		int numShips = Math.min((int) Math.ceil(currentSpawnNumber * Math.abs(rand.nextGaussian())), 5);
		int numPoints = (int) (Math.ceil(totalTimeMin/60f) * Math.abs(rand.nextGaussian()));
		if (numPoints < 2) numPoints = 2;
		else if (numPoints > 4) numPoints = 4;
		numPoints = 4;
		return new EnemyWave(numShips, listener, uiListener, generatePoints(numPoints));
	}
	
	private static Vector2[] generatePoints(int num) {
		Vector2[] points = new Vector2[num];
		
		int r = rand.nextInt(4);
		Side side = sides.removeIndex(r);
		points[0] = generatePoint(side, true);
		for (int i = 1; i < num - 1; i++) {
			int j = rand.nextInt(3);
			Side side2 = sides.removeIndex(j);
			sides.add(side);
			side = side2;
			points[i] = generatePoint(side, false);
		}
		sides.add(side);
		side = sides.removeIndex(rand.nextInt(4));
		points[num - 1] = generatePoint(side, true);
		sides.add(side);
		return points;
	}
	
	private static Vector2 generatePoint(Side s, boolean out) {
		Vector2 point = new Vector2();
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		if (s == Side.Left) {
			point.x = out ? -MAX_SHIP_SIZE : 0;
			point.y = rand.nextFloat() * height;
		} else if (s == Side.Top) {
			point.x = rand.nextFloat() * width;
			point.y = out ? height + MAX_SHIP_SIZE : height - MAX_SHIP_SIZE;
		} else if (s == Side.Right) {
			point.x = out ? width + MAX_SHIP_SIZE : width - MAX_SHIP_SIZE;
			point.y = rand.nextFloat() * height - MAX_SHIP_SIZE;
		} else if (s == Side.Bottom) {
			point.x = rand.nextFloat() * width - MAX_SHIP_SIZE;
			point.y = out ? -MAX_SHIP_SIZE : 0;
		}
		return point;
	}
	
	private static final Vector2 NORMAL = new Vector2(0, -1);
	private static final Rectangle tmpr = new Rectangle();
	
	private final EnemyShip[] ships;
	private final Array<EnemyShip> deadShips = new Array<EnemyShip>();
	private final Bezier<Vector2> path;
	private static final Vector2 tmp = new Vector2();
	private static final Vector2 tmp2 = new Vector2();
	private final float onScreenTime = 5f;
	private float timeElapsed;
	
	private UIListener uiListener;
	
	private static String[] enemyColourMap = new String[] {EnemyShip.BLUE, EnemyShip.GREEN, EnemyShip.RED, EnemyShip.BLACK};
	private static EnemyShip.EnemyType[] enemyTypeMap = new EnemyShip.EnemyType[] {EnemyShip.EnemyType.TYPE_2, EnemyShip.EnemyType.TYPE_3, EnemyShip.EnemyType.TYPE_4, EnemyShip.EnemyType.TYPE_5};
	
	public EnemyWave(int numShips, LaserListener listener, UIListener uiListener, Vector2 ... points) {
		this.ships = new EnemyShip[numShips];
		this.path = new Bezier<Vector2>(points);
		for (int i = 0; i < numShips; i++) {
			ships[i] = new EnemyShip(enemyColourMap[rand.nextInt(enemyColourMap.length)], enemyTypeMap[(int) Math.min(Math.abs(rand.nextGaussian()) + totalTimeMin/2, 3)], listener, uiListener);
		}
		this.uiListener = uiListener;
		update(0, 0, 0);
 	}
	
	public boolean checkCollision(Laser laser) {
		tmpr.x = laser.getX();
		tmpr.y = laser.getY();
		tmpr.width = laser.getRegionWidth();
		tmpr.height = laser.getRegionHeight();
		for (int i = 0; i < ships.length; i++) {
			EnemyShip enemy = ships[i];
			if (enemy == null) continue;
			boolean hit = AlgebraUtils.colliding(tmpr, laser.getRotation(), enemy.getHitBox());
			if (hit) {
				boolean dead = enemy.damage(laser.getDamageDealt());
				if (dead) {
					if (uiListener != null) uiListener.onScoreChange(ships[i].getPoints());
					deadShips.add(ships[i]);
					ships[i].modulation = EnemyShip.FADE;
					ships[i] = null;
					deadSound.play();
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean noMoreShips() {
		for (EnemyShip enemy: ships) {
			if (enemy != null) {
				boolean onScreen = SpriteUtils.onScreen(enemy);
				if (onScreen) return false;
			}
		}
		if (timeElapsed >= onScreenTime) return true;
		return false;
	}
	
	public void update(float delta, float xLookAt, float yLookAt) {
		timeElapsed += delta;
		for (int i = ships.length - 1; i >=0; i--) {
			if (ships[i] == null) continue;
			float t = timeElapsed/onScreenTime - i * 0.2f;
			if (t < 0) t = 0;
			if (t > 1) t = 1;
			Vector2 p = path.valueAt(tmp, t);
			SpriteUtils.center(ships[i], p.x, p.y);
			ships[i].getHitBox().x = p.x;
			ships[i].getHitBox().y = p.y;
			tmp.set(xLookAt, yLookAt).sub(tmp2.set(ships[i].getX(), ships[i].getY()));
			ships[i].setRotation(AlgebraUtils.angle(tmp, NORMAL));
			ships[i].update(delta);
		}
		for (EnemyShip deadShip: deadShips) {
			deadShip.update(delta);
		}
	}
	
	public void debug(ShapeRenderer s) {
		for (EnemyShip ship: ships) {
			if (ship != null) s.circle(ship.getHitBox().x, ship.getHitBox().y, ship.getHitBox().radius);
		}
	}
	
	public void draw(SpriteBatch batch) {
		for (EnemyShip ship: ships) {
			if (ship != null) {
				ship.draw(batch);
			}
		} 
		for (EnemyShip deadShip: deadShips) {
			deadShip.draw(batch);
		}
	}

}
