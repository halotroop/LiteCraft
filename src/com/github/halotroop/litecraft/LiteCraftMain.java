package com.github.halotroop.litecraft;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;

public class LiteCraftMain
{
	Window win;
	public void run()
	{
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		win = new Window();
		loop();

		// Free the window callbacks and destroy the window
		Callbacks.glfwFreeCallbacks(win.getWindow());
		GLFW.glfwDestroyWindow(win.getWindow());

		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}
	
	private void loop()
	{
		// This line is critical for LWJGL's interoperation with GLFW's
				// OpenGL context, or any context that is managed externally.
				// LWJGL detects the context that is current in the current thread,
				// creates the GLCapabilities instance and makes the OpenGL
				// bindings available for use.
				GL.createCapabilities();

				// Set the clear color
				GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

				// Run the rendering loop until the user has attempted to close
				// the window or has pressed the ESCAPE key.
				while ( !GLFW.glfwWindowShouldClose(win.getWindow()) ) {
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer

					GLFW.glfwSwapBuffers(win.getWindow()); // swap the color buffers

					// Poll for window events. The key callback above will only be
					// invoked during this call.
					GLFW.glfwPollEvents();
					
				}
	}

	public static void main(String[] args)
	{
		new LiteCraftMain().run();
	}
}