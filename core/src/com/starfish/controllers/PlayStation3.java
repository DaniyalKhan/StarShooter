package com.starfish.controllers;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;

public class PlayStation3 extends ControllerAdapter {

	public static final int LEFT_AXIS_X = 0;
	public static final int LEFT_AXIS_Y = 1;
	public static final int L1 = 4;
	public static final int R1 = 5;
	public static final int L2 = 6;
	public static final int R2 = 7;
	public static final int CIRCLE = 2;
	public static final int CROSS = 1;
	public static final int SQUARE = 0;
	public static final int TRIANGLE = 3;
	public static final int SELECT = 8;
	public static final int START = 9;
	
	Controller controller;
	
	PS3ButtonCallback callback;
	
	public PlayStation3(PS3ButtonCallback callback) {
		this.callback = callback;
		this.controller = Controllers.getControllers().first();
		System.err.print("WARNING!!! NO CONTROLLER PS3 CONTROLLER DETECTED!");
	}

	public float pollLeftAxisX() {
		if (controller == null) return 0;
		return -(controller.getAxis(LEFT_AXIS_X) * 100) / 100f;
	}
	
	public float pollLeftAxisY() {
		if (controller == null) return 0;
		return -(controller.getAxis(LEFT_AXIS_Y) * 100) / 100f;
	}
	
	public boolean pollL2() {
		if (controller == null) return false;
		return controller.getButton(L2);
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonIndex) {
		if (callback != null) {
			if (buttonIndex == CIRCLE) callback.onCircle();
			else if (buttonIndex == SQUARE) callback.onSquare();
			else if (buttonIndex == CROSS) callback.onCross();
			else if (buttonIndex == TRIANGLE) callback.onTriangle();
			else if (buttonIndex == START) callback.onStart();
		}
		return true;
	}
	
	public interface PS3ButtonCallback {
		public void onCircle();
		public void onSquare();
		public void onCross();
		public void onTriangle();
		public void onStart();
	}
	
}
