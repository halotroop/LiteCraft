package com.github.halotroop.litecraft;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.github.halotroop.litecraft.logic.TickListener;
import com.github.halotroop.litecraft.logic.Timer;

public class LiteCraftMain
{
	protected Timer timer;
	int i = 0;
	protected TickListener tickListener = new TickListener()
	{
		@Override
		public void onTick(float deltaTime)
		{
			i++;
			System.out.println("Ticking: " + String.valueOf(i));
		}
	};
	public int maxFPS = 60;
	public static int width = 400, height = 300; // Don't change these values. They just initialize it in case we forget to set them later.
	private Window window;
	private static boolean spamLog;
	
	public static void main(String[] args)
	{
		width = 1600;
		height = 900;
		spamLog = true;
		new LiteCraftMain().run();
	}

	public void run()
	{
		System.out.println("Running program.");
		System.out.println("LWJGL version: " + Version.getVersion());
		
		init();

		// Run the rendering loop until the player has attempted to close the window
		while (!GLFW.glfwWindowShouldClose(window.getWindowLong()))
		{
			loop();
		}
		
		
		destroy();
	}

	private void init()
	{
		System.out.println("Initializing game.");
		// Setup an error callback. The default implementation will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		// Configure GLFW
		window = new Window(width, height);
		timer = new Timer(20);
		timer.addTickListener(tickListener);
		
		GL.createCapabilities();	// This line is critical for LWJGL's interoperation with GLFW.
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);	// Set the background color

		window.setWindowTitle("LiteCraft " + "INSERT SPLASH TEXT HERE!");
		input();
	}

	// Sets up the key inputs for the game (currently just esc for closing the game)
	public void input()
	{
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		GLFW.glfwSetKeyCallback(window.getWindowLong(), (window, key, scancode, action, mods) ->
		{
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
	}
	
	// Things that the game should do over and over and over again until it is closed
	private void loop()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		GLFW.glfwSwapBuffers(window.getWindowLong()); // swap the color buffers
		// Poll for window events. The key callback above will only be invoked during this call.
		GLFW.glfwPollEvents();
		timer.update();
	}

	public void render()
	{
		if (spamLog)
		window.render();
	}
	
	// Shuts down the game and destroys all the things that are using RAM (so the user doesn't have to restart their computer afterward...)
	private void destroy()
	{
		System.out.println("Closing game...");
		window.destroy();
		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
		System.out.println("Game closed successfully.");
	}
}