package com.github.halotroop.litecraft;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;

public class LiteCraftMain
{
	Window win;

	private void init()
	{
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		// Configure GLFW
		GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable
		win = new Window();
		// Make the OpenGL context current
		GLFW.glfwMakeContextCurrent(win.getWindow());
		// Enable v-sync
		GLFW.glfwSwapInterval(1);
		// Make the window visible
		GLFW.glfwShowWindow(win.getWindow());
	}

	public void run()
	{
		System.out.println("LWJGL version: " + Version.getVersion());
		init();
		loop();
		destroy();
	}

	private void destroy()
	{
		win.destroy();
		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	private void loop()
	{
		// This line is critical for LWJGL's interoperation with GLFW.
		GL.createCapabilities();
		// Set the background color
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// Run the rendering loop until the user has attempted to close the window or has pressed the ESCAPE key.
		while (!GLFW.glfwWindowShouldClose(win.getWindow()))
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			GLFW.glfwSwapBuffers(win.getWindow()); // swap the color buffers
			// Poll for window events. The key callback above will only be invoked during this call.
			GLFW.glfwPollEvents();
		}
	}

	public static void main(String[] args)
	{ new LiteCraftMain().run(); }
}