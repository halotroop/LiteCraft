package com.github.halotroop.litecraft.render;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.github.halotroop.litecraft.LiteCraftMain;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;
import io.github.hydos.ginger.engine.elements.GuiTexture;
import io.github.hydos.ginger.engine.elements.objects.Entity;
import io.github.hydos.ginger.engine.elements.objects.Light;
import io.github.hydos.ginger.engine.elements.objects.RenderPlayer;
import io.github.hydos.ginger.engine.font.TextMaster;
import io.github.hydos.ginger.engine.io.Window;
import io.github.hydos.ginger.engine.particle.ParticleMaster;
import io.github.hydos.ginger.engine.postprocessing.PostProcessing;
import io.github.hydos.ginger.engine.render.MasterRenderer;
import io.github.hydos.ginger.engine.terrain.Terrain;
import io.github.hydos.ginger.engine.utils.Loader;
import io.github.hydos.ginger.main.GingerMain;
/*
 * The non kid friendly wrapper for your blocky game
 */

public class RenderWrapper
{
	private static MasterRenderer masterRenderer;
	public static List<Entity> entities = new ArrayList<Entity>();
	public static List<GuiTexture> guis = new ArrayList<GuiTexture>();
	public static List<Light> lights = new ArrayList<Light>();
	public static ThirdPersonCamera camera;
	private static final List<Terrain> TERRAIN = new ArrayList<Terrain>();
	private static final List<Entity> NORMAL_ENTITY = new ArrayList<Entity>();

	public static void init(String splash, RenderPlayer renderPlayer)
	{
		camera = new ThirdPersonCamera(new Vector3f(0, 0.1f, 0), renderPlayer);
		Window.setBackgroundColour(96 / 256F, 26 / 256F, 108 / 25F);
		masterRenderer = new MasterRenderer(camera);
		ParticleMaster.init(masterRenderer.getProjectionMatrix());
		PostProcessing.init();
	}

	public static void cleanup()
	{
		Window.stop();
		PostProcessing.cleanUp();
		ParticleMaster.cleanUp();
		masterRenderer.cleanUp();
		TextMaster.cleanUp();
		Loader.cleanUp();
		System.exit(0);
	}

	public static void render()
	{
		Window.update();
		GingerMain.update();
		GingerMain.preRenderScene(masterRenderer);
		masterRenderer.renderScene(entities, NORMAL_ENTITY, TERRAIN, lights, camera, new Vector4f(0, -1, 0, 100000));
		ParticleMaster.renderParticles(camera);
		masterRenderer.renderGuis(guis);
		TextMaster.render();
		Window.swapBuffers();
	}

	public static void preInit()
	{
		Window.create(LiteCraftMain.width, LiteCraftMain.height, "LiteCraft - " + LiteCraftMain.splashText, 60);
		GingerMain.init();
	}

	public static void update()
	{
		Window.update();
		GingerMain.update();
		ParticleMaster.update(camera);
	}
}
