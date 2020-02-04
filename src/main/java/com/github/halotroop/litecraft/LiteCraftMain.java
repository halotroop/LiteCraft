package com.github.halotroop.litecraft;

import org.apache.commons.cli.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.github.halotroop.litecraft.logic.Timer;
import com.github.halotroop.litecraft.logic.Timer.TickListener;
import com.github.halotroop.litecraft.render.Renderer;

public class LiteCraftMain implements Runnable
{
	public static int maxFPS = 60;
	public static int width = 640, height = 480; // Don't change these values. They just initialize it in case we forget to set them later.
	public static boolean spamLog, debug;
	public String splashText = "";

	protected Timer timer;
	private int fps, ups, tps;
	private long frameTimer;
	private Renderer renderer;

	private Window window;
	
	public static void main(String[] args) throws Exception
	{
		Options options = new Options();
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;
		options.addOption(new Option("w", "width", true, "Screen width"));
		options.addOption(new Option("h", "height", true, "Screen height"));
		options.addOption(new Option("debug", "debug", true, "Use debug features"));
		options.addOption(new Option("spam_log","spam_log", true, "Log sanity checks"));
		options.addOption(new Option("limit_fps", "limit_fps", true, "Use the FPS limiter"));
		options.addOption(new Option("max_fps", "max_fps", true, "The maximum amount of FPS"));
		
		try
		{
			cmd = parser.parse(options, args);
			width = Integer.parseInt(cmd.getOptionValue("width", "640"));
			height = Integer.parseInt(cmd.getOptionValue("height", "480"));
			debug = Boolean.parseBoolean(cmd.getOptionValue("debug", "false"));
			spamLog = Boolean.parseBoolean(cmd.getOptionValue("spam_log", "false"));
			maxFPS = Integer.parseInt(cmd.getOptionValue("max_fps", "60"));
		}
		catch (ParseException e)
		{
			System.out.println(e.getLocalizedMessage());
			formatter.printHelp("utility-name", options);
		}
		
		
		new LiteCraftMain().run();
	}
	
	protected TickListener tickListener = new TickListener()
	{
		@Override
		public void onTick(float deltaTime)
		{ tps++; }
	};


	// Shuts down the game and destroys all the things that are using RAM (so the user doesn't have to restart their computer afterward...)
	private void destroy()
	{
		if (debug) System.out.println("Closing game...");
		renderer.cleanUp();
		window.destroy();
		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
		if (debug) System.out.println("Game closed successfully.");
	}

	private void init()
	{
		if (debug) System.out.println("Initializing game...");
		// Setup an error callback. The default implementation will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		// Configure GLFW
		window = new Window(width, height);
		timer = new Timer(20);
		timer.addTickListener(tickListener);
		GL.createCapabilities(); // This line is critical for LWJGL's interoperation with GLFW.
		renderer = new Renderer(); 
		if (splashText == "")
		{
			window.setWindowTitle("LiteCraft - " + "INSERT SPLASH TEXT HERE!");	
		}
		input();
		if (debug) System.out.println("Initialization complete.");
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
			if (debug) window.setWindowTitle("LiteCraft | FPS: " + fps + " | TPS: " + tps + " | UPS: " + ups);
			fps = 0;
			ups = 0;
			tps = 0;
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

	public void run()
	{
		System.out.println("Starting game...");
		System.out.println("LWJGL version: " + Version.getVersion());
		System.out.println("Resolution: " + width + 'x' + height);
		init();
		frameTimer = System.currentTimeMillis();
		// Run the rendering loop until the player has attempted to close the window
		while (!GLFW.glfwWindowShouldClose(window.getWindowLong()))
		{ loop(); }
		destroy();
	}
}
