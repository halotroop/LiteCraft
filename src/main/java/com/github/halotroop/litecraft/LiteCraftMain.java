package com.github.halotroop.litecraft;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Random;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.github.halotroop.litecraft.input.Input;
import com.github.halotroop.litecraft.input.Keybind;
import com.github.halotroop.litecraft.logic.Timer;
import com.github.halotroop.litecraft.logic.Timer.TickListener;
import com.github.halotroop.litecraft.options.SettingsConfig;
import com.github.halotroop.litecraft.options.SettingsHandler;
import com.github.halotroop.litecraft.render.Renderer;

public class LiteCraftMain implements Runnable
{
	public static Logger logger = LogManager.getLogger(Logger.class.getName());
	private static SettingsConfig config;
	public static int width = 640, height = 480, maxFPS = 60; // Don't change these values. They just initialize it in case we forget to set them later.
	public static boolean spamLog = false, debug = false, limitFPS = false;
	public String splashText = "";
	private int fps, ups, tps;
	private long frameTimer;
	private static Renderer renderer;
	private static Window window;
	protected Timer timer;
	protected TickListener tickListener = new TickListener()
	{
		@Override
		public void onTick(float deltaTime)
		{ tps++; }
	};

	public static void main(String[] args) throws Exception
	{
		try
		{
			config = ConfigFactory.create(SettingsConfig.class);
			width = config.screenWidth();
			height = config.screenHeight();
			maxFPS = config.max_fps();
			spamLog = config.spamLog();
			debug = config.debugMode();
		}
		catch (Exception e)
		{
			System.err.println("Config failed to load.");
			e.printStackTrace();
		}
		try
		{
			Options options = SettingsHandler.createCommandLineOptions();
			CommandLine cmd = new DefaultParser().parse(options, args);
			width = Integer.parseInt(cmd.getOptionValue("width", "640"));
			height = Integer.parseInt(cmd.getOptionValue("height", "480"));
			maxFPS = Integer.parseInt(cmd.getOptionValue("max_fps", "60"));
			debug = Boolean.parseBoolean(cmd.getOptionValue("debug", "false"));
			spamLog = Boolean.parseBoolean(cmd.getOptionValue("spam_log", "false"));
			limitFPS = Boolean.parseBoolean(cmd.getOptionValue("limit_fps", "false"));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		new LiteCraftMain().run();
	}

	private void init()
	{
		// Leave this alone.
		GLFWErrorCallback.createPrint(System.err).set();
		if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		window = new Window(width, height);
		// Please and thank you. :)

		GL.createCapabilities(); // This line is critical for LWJGL's interoperation with GLFW.
		renderer = new Renderer();
		timer = new Timer(20);
		timer.addTickListener(tickListener);
		
		try
		{
			String[] splashes = TextFileReader.readFileToStringArray("text/splashes.txt");
			splashText = splashes[new Random().nextInt(splashes.length)];
		}
		catch (IOException e)
		{ e.printStackTrace(); }
		window.setWindowTitle("LiteCraft - " + ((splashText == "" || splashText == null) ? "INSERT SPLASH TEXT HERE!" : splashText));
		input();
	}

	// Sets up the key inputs for the game (currently just esc for closing the game)
	public void input()
	{
		Input.addPressCallback(Keybind.EXIT, LiteCraftMain::shutDown);
	}

	// Things that the game should do over and over and over again until it is closed
	private void loop()
	{
		ups++;
		// Poll for window events. The key callback above will only be invoked during this call.
		GLFW.glfwPollEvents();
		Input.invokeAllListeners();
		timer.tick();
		if (fps < maxFPS || !limitFPS) render();
		if (System.currentTimeMillis() > frameTimer + 1000) // wait for one second
		{
			fps = 0;
			ups = 0;
			tps = 0;
			frameTimer += 1000; // reset the wait time
		}
	}

	public void render()
	{
		if (debug) window.setWindowTitle("LiteCraft | FPS: " + fps + " | TPS: " + tps + " | UPS: " + ups);
		renderer.render();
		window.render();
		fps++; // After a successful frame render, increase the frame counter.
	}

	public void run()
	{
		System.out.println("Starting game..." + "\n" + "LWJGL version: " + Version.getVersion() + "\n" + "Resolution: " + width + 'x' + height);
		init();
		frameTimer = System.currentTimeMillis();
		// Run the rendering loop until the player has attempted to close the window
		while (!GLFW.glfwWindowShouldClose(window.getWindowId()))
		{ loop(); }
		shutDown();
	}

	// Shuts down the game and destroys all the things that are using RAM (so the user doesn't have to restart their computer afterward...)
	private static void shutDown()
	{
		logger.log(Level.DEBUG, "Closing game...");
		renderer.cleanUp();
		window.destroy();
		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
		System.out.println("Game closed successfully.");
		System.exit(0);
	}
}
