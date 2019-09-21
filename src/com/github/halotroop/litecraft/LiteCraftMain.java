package com.github.halotroop.litecraft;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.github.halotroop.litecraft.logic.TickListener;
import com.github.halotroop.litecraft.logic.Timer;
import com.github.halotroop.litecraft.render.Renderer;

public class LiteCraftMain
{
	protected Timer timer;
	private int fps, ups;
	public static int maxFPS = 100;
	private long frameTimer;
	private Renderer renderer;
	
	public static int width = 400, height = 300; // Don't change these values. They just initialize it in case we forget to set them later.
	private Window window;
	private static boolean spamLog;
	
	protected TickListener tickListener = new TickListener()
	{
		@Override
		public void onTick(float deltaTime)
		{
			
		}
	};
	
	
	public static void main(String[] args)
	{
		width = 1600;
		height = 900;
		spamLog = false;
		new LiteCraftMain().run();
	}

	public void run()
	{
		System.out.println("Running program.");
		System.out.println("LWJGL version: " + Version.getVersion());
		
		init();

		frameTimer = System.currentTimeMillis();
		
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
		renderer = new Renderer();

		window.setWindowTitle("LiteCraft " + "INSERT SPLASH TEXT HERE!");
		input();
		
		System.out.println("Initialization complete.");
	}

	// Sets up the key inputs for the game (currently just esc for closing the game)
	public void input()
	{
		// A temporary key callback. It will tell GLFW to close the window whenever we press escape
		GLFW.glfwSetKeyCallback(window.getWindowLong(), (window, key, scancode, action, mods) ->
		{
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the game loop
		});
	}
	
	// Things that the game should do over and over and over again until it is closed
	private void loop()
	{
		ups++;
		// Poll for window events. The key callback above will only be invoked during this call.
		GLFW.glfwPollEvents();
		
		timer.tick();
		
		if (fps < maxFPS) render();
		
		if (System.currentTimeMillis() > frameTimer + 1000) // wait for one second
		{
			System.out.println("Frames this second: " + fps);
			System.out.println("Updates this second: " + ups);
			fps = 0;
			ups = 0;
			frameTimer += 1000; // reset the wait time
		}
	}

	public void render()
	{
		if (spamLog) System.out.println("rendering " + fps);

		renderer.render();
		window.render();
		fps++; // After a successful frame render, increase the frame counter.
	}
	
	// Shuts down the game and destroys all the things that are using RAM (so the user doesn't have to restart their computer afterward...)
	private void destroy()
	{
		System.out.println("Closing game...");
		
		renderer.cleanUp();
		window.destroy();
		
		
		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
		System.out.println("Game closed successfully.");
	}
	
}
