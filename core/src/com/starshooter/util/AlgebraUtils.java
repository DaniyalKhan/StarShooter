package com.starshooter.util;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class AlgebraUtils {

	public static final Vector2 TERMINAL_ARM = new Vector2(1, 0);
	private static final Vector2 tmp = new Vector2();
	private static final Circle tmpc = new Circle();
	
	public static float angle(Vector2 v1, Vector2 v2) {
		return (float) (360 - 180 * Math.atan2(v1.crs(v2), v1.dot(v2))/ MathUtils.PI);
	}
	
	public static float angleFromTermninal(Vector2 v) {
		return angle(v, TERMINAL_ARM);
	}
	
	public static float normalizeAngle(float theta) {
		if (theta < 0) {
			while (theta < 0) theta +=360;
		} else if (theta >= 360) {
			while (theta >= 360) theta -=360;
		}
		return theta;
	}
	
	public static boolean colliding(Rectangle r, float theta, Circle c) {
		theta = normalizeAngle(theta);
		Vector2 rotated = rotateAround(c.x, c.y, SpriteUtils.getMid(r).x, SpriteUtils.getMid(r).y, theta);
		tmpc.set(rotated.x, rotated.y, c.radius);
		return Intersector.overlaps(tmpc, r);
	}
	
	public static Vector2 rotateAround(float v1x, float v1y, float v2x, float v2y, float theta) {
		float cos = MathUtils.cosDeg(theta);
		float sin = MathUtils.sinDeg(theta);
		float dx = v1x - v2x;
		float dy = v1y - v2y;
		return tmp.set(cos * dx - sin * dy + v2x, sin * dx + cos * dy + v2y);
	}
	
}
