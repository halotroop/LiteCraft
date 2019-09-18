package com.github.halotroop.litecraft;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

public class LiteCraftMain
{
	protected LCWindow window;
	public static int width = 400, height = 300; // Don't change these values. They just initialize it in case we forget to set them later.

	public void run()
	{
		System.out.println("LWJGL version: " + Version.getVersion());
		
		init();
		loop();
		
		destroy();
	}
	
	private void init()
	{
		// Setup an error callback. The default implementation will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		// Configure GLFW
		window = new LCWindow(width, height);
		window.setDefaultTitle("LiteCraft " + "INSERT SPLASH TEXT HERE!");
		
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		GLFW.glfwSetKeyCallback(window.getWindowLong(), (window, key, scancode, action, mods) ->
		{
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
	}

	private void destroy()
	{
		window.dispose();
		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	private void loop()
	{
		// This line is critical for LWJGL's interoperation with GLFW.
		GL.createCapabilities();
		// Set the background color
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key.
		while (!GLFW.glfwWindowShouldClose(window.getWindowLong()))
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			GLFW.glfwSwapBuffers(window.getWindowLong()); // swap the color buffers
			// Poll for window events. The key callback above will only be invoked during this call.
			GLFW.glfwPollEvents();
		}
	}

	public static void main(String[] args)
	{
		width = 1600;
		height = 900;
		new LiteCraftMain().run();
	}
}