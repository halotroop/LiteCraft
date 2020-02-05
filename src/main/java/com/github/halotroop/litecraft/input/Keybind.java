package com.github.halotroop.litecraft.input;

import org.lwjgl.glfw.GLFW;

/*
 * Author: Valoeghese
 */
public final class Keybind
{
	public int value;
	public boolean mouse;
	public static final Keybind MOVE_UP = new Keybind(GLFW.GLFW_KEY_W, false);
	public static final Keybind MOVE_DOWN = new Keybind(GLFW.GLFW_KEY_S, false);
	public static final Keybind MOVE_LEFT = new Keybind(GLFW.GLFW_KEY_A, false);
	public static final Keybind MOVE_RIGHT = new Keybind(GLFW.GLFW_KEY_D, false);
	public static final Keybind USE = new Keybind(GLFW.GLFW_MOUSE_BUTTON_1, true);
	public static final Keybind SELECT_0 = new Keybind(GLFW.GLFW_KEY_1, false);
	public static final Keybind SELECT_1 = new Keybind(GLFW.GLFW_KEY_2, false);
	public static final Keybind SELECT_2 = new Keybind(GLFW.GLFW_KEY_3, false);
	public static final Keybind EXIT = new Keybind(GLFW.GLFW_KEY_ESCAPE, false);

	public Keybind(int initValue, boolean isMouse)
	{
		this.value = initValue;
		this.mouse = isMouse;
	}

	public boolean isActive()
	{ return mouse ? MouseCallbackHandler.buttons[value] : KeyCallbackHandler.keys[value]; }
}
