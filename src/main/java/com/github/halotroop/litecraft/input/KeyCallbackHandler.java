package com.github.halotroop.litecraft.input;

import org.lwjgl.glfw.*;

/*
 * Author: Valoeghese
 */
public class KeyCallbackHandler extends GLFWKeyCallback
{
	private KeyCallbackHandler()
	{}

	private static final KeyCallbackHandler INSTANCE = new KeyCallbackHandler();

	public static void trackWindow(long window)
	{ GLFW.glfwSetKeyCallback(window, INSTANCE); }

	public static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods)
	{
		try
		{
			keys[key] = action != GLFW.GLFW_RELEASE;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// Probably just changing the volume
		}
	}
}
