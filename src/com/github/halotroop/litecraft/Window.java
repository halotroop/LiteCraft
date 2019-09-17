package com.github.halotroop.litecraft;

import java.nio.IntBuffer;

import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

public class Window
{
	private long window;

	public long getWindow()
	{ return window; }

	public void setWindow(long window)
	{ this.window = window; }

	public Window()
	{
		// Create the window
		window = GLFW.glfwCreateWindow(1600, 900, "Hello World!", 0, 0);
		if (window == 0)
			throw new RuntimeException("Failed to create the GLFW window");
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) ->
		{
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		// Get the thread stack and push a new frame
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*
			// Get the window size passed to glfwCreateWindow
			GLFW.glfwGetWindowSize(window, pWidth, pHeight);
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			// Center the window
			GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
			// Make the OpenGL context current
			GLFW.glfwMakeContextCurrent(window);
			// Enable v-sync
			GLFW.glfwSwapInterval(1);
			// Make the window visible
			GLFW.glfwShowWindow(window);
		}
	}

	public void destroy()
	{
		// Free the window callbacks and destroy the window
		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwDestroyWindow(window);
	}
}
