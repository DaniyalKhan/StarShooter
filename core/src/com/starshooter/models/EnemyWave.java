package com.starshooter.models;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
	
	public static EnemyWave update(float delta, LaserListener listener, UIListener uiListener) {
		totalTimeMin += delta/60f;
		currentSpawnNumber = Math.max(MathUtils.round(totalTimeMin) * 2, 2);
		LastSpawnTime += delta;
		if (LastSpawnTime >= spawnThreshold) {
			LastSpawnTime = 0;
			int numShips = Math.min((int) Math.ceil(currentSpawnNumber * Math.abs(rand.nextGaussian())), 16);
			int numPoints = (int) (Math.ceil(totalTimeMin/60f) * Math.abs(rand.nextGaussian()));
			if (numPoints < 2) numPoints = 2;
			else if (numPoints > 4) numPoints = 4;
			numPoints = 4;
			return new EnemyWave(numShips, listener, uiListener, generatePoints(numPoints));
		}
		return null;
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
			point.y = out ? height + MAX_SHIP_SIZE : height;
		} else if (s == Side.Right) {
			point.x = out ? width + MAX_SHIP_SIZE : width;
			point.y = rand.nextFloat() * height;
		} else if (s == Side.Bottom) {
			point.x = rand.nextFloat() * width;
			point.y = out ? -MAX_SHIP_SIZE : 0;
		}
		return point;
	}
	
	private static final Vector2 NORMAL = new Vector2(0, -1);
	private static final Rectangle tmpr = new Rectangle();
	
	private final EnemyShip[] ships;
	private final Bezier<Vector2> path;
	private static final Vector2 tmp = new Vector2();
	private static final Vector2 tmp2 = new Vector2();
	private final float onScreenTime = 5f;
	private float timeElapsed;
	
	private UIListener uiListener;
	
	public EnemyWave(int numShips, LaserListener listener, UIListener uiListener, Vector2 ... points) {
		this.ships = new EnemyShip[numShips];
		this.path = new Bezier<Vector2>(points);
		for (int i = 0; i < numShips; i++) {
			ships[i] = new EnemyShip(EnemyShip.BLACK, EnemyShip.EnemyType.TYPE_4, listener, uiListener);
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
					ships[i] = null;
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
			Vector2 p = path.valueAt(tmp, t);
			SpriteUtils.center(ships[i], p.x, p.y);
			ships[i].getHitBox().x = p.x;
			ships[i].getHitBox().y = p.y;
			tmp.set(xLookAt, yLookAt).sub(tmp2.set(ships[i].getX(), ships[i].getY()));
			ships[i].setRotation(AlgebraUtils.angle(tmp, NORMAL));
			ships[i].update(delta);
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
	}

}
