package com.github.halotroop.litecraft;

import java.io.IOException;
import java.util.Random;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import com.github.halotroop.litecraft.blaze4D.RenderWrapper;
import com.github.halotroop.litecraft.input.Input;
import com.github.halotroop.litecraft.input.Keybind;
import com.github.halotroop.litecraft.logic.Timer;
import com.github.halotroop.litecraft.logic.Timer.TickListener;
import com.github.halotroop.litecraft.options.SettingsConfig;
import com.github.halotroop.litecraft.options.SettingsHandler;

import io.github.hydos.ginger.engine.elements.objects.RenderPlayer;
import io.github.hydos.ginger.engine.io.Window;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector3f;
import io.github.hydos.ginger.engine.obj.ModelLoader;
import io.github.hydos.ginger.engine.renderEngine.models.TexturedModel;

public class LiteCraftMain implements Runnable
{
	public static Logger logger = Logger.getLogger(Logger.class.getName());
	private static SettingsConfig config;
	public static int width = 640, height = 480, maxFPS = 60; // Don't change these values. They just initialize it in case we forget to set them later.
	public static boolean spamLog = false, debug = false, limitFPS = false;
	public String splashText = "";
	@SuppressWarnings("unused")
	private int fps, ups, tps;
	private long frameTimer;
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
		logger.setLevel(debug ? Level.ALL : Level.INFO);
		GLFWErrorCallback.createPrint(System.err).set();
		if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
		
		timer = new Timer(20);
		timer.addTickListener(tickListener);
		
		try
		{
			String[] splashes = TextFileReader.readFileToStringArray("text/splashes.txt");
			splashText = splashes[new Random().nextInt(splashes.length)];
		}
		catch (IOException e)
		{ e.printStackTrace(); }
		
		//because someone has not made player models and im lazy lets use the ones bundeled with the engine :)
		
		RenderWrapper.preInit();
		
		TexturedModel tModel = ModelLoader.loadModel("stall.obj", "stallTexture.png");
		tModel.getTexture().setReflectivity(1f);
		tModel.getTexture().setShineDamper(7f);
		RenderPlayer renderPlayer = new RenderPlayer(tModel, new Vector3f(0,0,-3),0,180f,0, new Vector3f(0.2f, 0.2f, 0.2f));
		
		RenderWrapper.init(splashText, renderPlayer);
		
		
//		window.setWindowTitle("LiteCraft - " + ((splashText == "" || splashText == null) ? "INSERT SPLASH TEXT HERE!" : splashText));
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
		RenderWrapper.update();
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
		if (debug) System.out.println("LiteCraft | FPS: " + fps + " | TPS: " + tps + " | UPS: " + ups);
		RenderWrapper.render();
		fps++; // After a successful frame render, increase the frame counter.
	}

	public void run()
	{
		System.out.println("Starting game..." + "\n" + "LWJGL version: " + Version.getVersion() + "\n" + "Resolution: " + width + 'x' + height);
		init();
		frameTimer = System.currentTimeMillis();
		// Run the rendering loop until the player has attempted to close the window
		while(!Window.closed()) {
			
			if(Window.isUpdating()) {
				loop();
			}
		}
		shutDown();
	}

	// Shuts down the game and destroys all the things that are using RAM (so the user doesn't have to restart their computer afterward...)
	private static void shutDown()
	{
		logger.log(Level.DEBUG, "Closing game...");
		RenderWrapper.cleanup();
		System.out.println("Game closed successfully.");
		System.exit(0);
	}
}
