package com.halotroop.litecraft;

import org.joml.*;

import com.github.hydos.ginger.engine.common.Constants;
import com.github.hydos.ginger.engine.common.api.*;
import com.github.hydos.ginger.engine.common.api.game.*;
import com.github.hydos.ginger.engine.common.cameras.*;
import com.github.hydos.ginger.engine.common.elements.objects.Light;
import com.github.hydos.ginger.engine.common.font.FontType;
import com.github.hydos.ginger.engine.common.info.RenderAPI;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.common.obj.ModelLoader;
import com.github.hydos.ginger.engine.opengl.api.GingerGL;
import com.github.hydos.ginger.engine.opengl.postprocessing.PostProcessing;
import com.github.hydos.ginger.engine.opengl.render.*;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;
import com.github.hydos.ginger.engine.opengl.utils.*;
import com.halotroop.litecraft.render.BlockRenderer;
import com.halotroop.litecraft.save.LitecraftSave;
import com.halotroop.litecraft.screens.*;
import com.halotroop.litecraft.types.block.Blocks;
import com.halotroop.litecraft.types.entity.PlayerEntity;
import com.halotroop.litecraft.util.RelativeDirection;
import com.halotroop.litecraft.world.World;

import tk.valoeghese.gateways.client.io.*;

public class Litecraft extends Game
{
	// FIXME: search for ((GingerGL)engine) and properly implement both render APIs when Vulkan is complete.
	private static Litecraft INSTANCE;
	private World world;
	private LitecraftSave save;
	private GingerEngine engine;
	public int fps, ups, tps;
	public Vector4i dbgStats = new Vector4i();
	private long frameTimer;
	private BlockRenderer blockRenderer;

	public Litecraft(int windowWidth, int windowHeight, float frameLimit)
	{
		Litecraft.INSTANCE = this;
		// set constants
		this.setupConstants();
		this.setupGinger(windowWidth, windowHeight, frameLimit);
		// make sure blocks are initialised ??? (Currently does nothing)
		Blocks.init();
		this.frameTimer = System.currentTimeMillis();
		// setup keybinds
		setupKeybinds();
		// Open the title screen if nothing is already open.
		if (GingerRegister.getInstance().currentScreen == null && world == null) ((GingerGL) engine).openScreen(new TitleScreen());
		// start the game loop
		this.engine.startGameLoop();
	}

	@Override
	public void exit()
	{
		engine.openScreen(new ExitGameScreen());
		render(); // Render the exit game screen
		if (this.world != null)
		{
			System.out.println("Saving chunks...");
			long time = System.currentTimeMillis();
			this.world.unloadAllChunks();
			this.getSave().saveGlobalData(this.world.getSeed(), ((PlayerEntity) this.player));
			System.out.println("Saved world in " + (System.currentTimeMillis() - time) + " milliseconds");
		}
		engine.cleanup();
		System.exit(0);
	}

	/** Things that ARE rendering: Anything that results in something being drawn to the frame buffer
	 * Things that are NOT rendering: Things that happen to update between frames but do not result in things being drawn to the screen */
	@Override
	public void render()
	{
		fps += 1;
		if (System.currentTimeMillis() > frameTimer + 1000) updateDebugStats();
		// Render shadows
		GingerRegister.getInstance().masterRenderer.renderShadowMap(data.entities, data.lights.get(0));
		// If there's a world, render it!
		if (this.world != null) renderWorld();
		// Render any overlays (GUIs, HUDs)
		this.engine.renderOverlays();
		// Put what's stored in the inactive framebuffer on the screen
		Window.swapBuffers();
	}

	// Updates the debug stats once per real-time second, regardless of how many frames have been rendered
	private void updateDebugStats()
	{
		this.dbgStats.set(fps, ups, tps, 0);
		this.fps = 0;
		this.ups = 0;
		this.tps = 0;
		this.frameTimer += 1000;
	}

	public void renderWorld()
	{
		GameData data = GingerRegister.getInstance().game.data;
		if (Window.renderAPI == RenderAPI.OpenGL)
		{
			GLUtils.preRenderScene(((GingerGL) engine).getRegistry().masterRenderer);
			((GingerGL) engine).contrastFbo.bindFBO();
			((GingerGL) engine).getRegistry().masterRenderer.renderScene(data.entities, data.normalMapEntities, data.lights, data.camera, data.clippingPlane);
			((GingerGL) engine).contrastFbo.unbindFBO();
			PostProcessing.doPostProcessing(((GingerGL) engine).contrastFbo.colorTexture);
		}
	}

	@Override
	public void update()
	{ ups += 1; }

	private void setupConstants()
	{
		Constants.movementSpeed = 0.5f; // movement speed
		Constants.turnSpeed = 0.00006f; // turn speed
		Constants.gravity = new Vector3f(0, -0.0000000005f, 0); // compute gravity as a vec3f
		Constants.jumpPower = 0.00005f; // jump power
	}

	// set up Ginger3D engine stuff
	private void setupGinger(int windowWidth, int windowHeight, float frameCap)
	{
		if (engine == null) // Prevents this from being run more than once on accident.
		{
			Window.create(windowWidth, windowHeight, "Litecraft", frameCap, RenderAPI.OpenGL); // create window
			// set up the gateways keybind key tracking
			KeyCallbackHandler.trackWindow(Window.getWindow());
			MouseCallbackHandler.trackWindow(Window.getWindow());
			// set up ginger utilities
			GLUtils.init();
			switch (Window.renderAPI)
			{
			case OpenGL:
			{
				this.engine = new GingerGL();
				//Set the player model
				GLTexturedModel playerModel = ModelLoader.loadGenericCube("block/cubes/stone/brick/stonebrick.png");
				FontType font = new FontType(GLLoader.loadFontAtlas("candara.png"), "candara.fnt");
				this.player = new PlayerEntity(playerModel, new Vector3f(0, 0, -3), 0, 180f, 0, new Vector3f(0.2f, 0.2f, 0.2f));
				this.camera = new FirstPersonCamera(player);
				this.data = new GameData(this.player, this.camera, 20);
				this.data.handleGuis = false;
				((GingerGL) engine).setup(new GLRenderManager(this.camera), INSTANCE);
				((GingerGL) engine).setGlobalFont(font);
				this.blockRenderer = new BlockRenderer(GingerRegister.getInstance().masterRenderer.getEntityShader(), GingerRegister.getInstance().masterRenderer.getProjectionMatrix());
				this.data.entities.add(this.player);
				break;
			}
			case Vulkan:
			{
				// TODO: Setup Vulkan
				exit();
				break;
			}
			}
			Light sun = new Light(new Vector3f(0, 105, 0), new Vector3f(0.9765625f, 0.98828125f, 0.05859375f), new Vector3f(0.002f, 0.002f, 0.002f));
			this.data.lights.add(sun);
		}
	}

	private void setupKeybinds()
	{
		Input.addPressCallback(Keybind.EXIT, this::exit);
		Input.addInitialPressCallback(Keybind.FULLSCREEN, Window::fullscreen);
		Input.addInitialPressCallback(Keybind.WIREFRAME, GingerRegister.getInstance()::toggleWireframe);
		Input.addPressCallback(Keybind.MOVE_FORWARD, () -> ((PlayerEntity) this.player).move(RelativeDirection.FORWARD));
		Input.addPressCallback(Keybind.MOVE_BACKWARD, () -> ((PlayerEntity) this.player).move(RelativeDirection.BACKWARD));
		Input.addPressCallback(Keybind.STRAFE_LEFT, () -> ((PlayerEntity) this.player).move(RelativeDirection.LEFT));
		Input.addPressCallback(Keybind.STRAFE_RIGHT, () -> ((PlayerEntity) this.player).move(RelativeDirection.RIGHT));
		Input.addPressCallback(Keybind.FLY_UP, () -> ((PlayerEntity) this.player).move(RelativeDirection.UP));
		Input.addPressCallback(Keybind.FLY_DOWN, () -> ((PlayerEntity) this.player).move(RelativeDirection.DOWN));
	}

	/** Things that should be ticked: Entities when deciding an action, in-game timers (such as smelting), the in-game time
	 * Things that should not be ticked: Rendering, input, player movement */
	@Override
	public void tick()
	{
		tps += 1;
		if (this.player instanceof PlayerEntity && camera != null)
		{
			Input.invokeAllListeners();
			((PlayerEntity) this.player).updateMovement();
			camera.updateMovement();
		}
	}

	// @formatter=off
	public static Litecraft getInstance()
	{ return INSTANCE; }

	public Camera getCamera()
	{ return this.camera; }

	public LitecraftSave getSave()
	{ return save; }

	public World getWorld()
	{ return this.world; }

	public void changeWorld(World world)
	{ this.world = world; }

	public void setSave(LitecraftSave save)
	{ this.save = save; }

	@Override
	public void renderScene()
	{ world.render(this.blockRenderer); }
}