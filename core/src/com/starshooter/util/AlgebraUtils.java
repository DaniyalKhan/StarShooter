package com.starshooter.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class AlgebraUtils {

	public static final Vector2 TERMINAL_ARM = new Vector2(1, 0);
	
	public static float angle(Vector2 v1, Vector2 v2) {
		return (float) (360 - 180 * Math.atan2(v1.crs(v2), v1.dot(v2))/ MathUtils.PI);
	}
	
	public static float angleFromTermninal(Vector2 v) {
		return angle(v, TERMINAL_ARM);
	}
	
}
