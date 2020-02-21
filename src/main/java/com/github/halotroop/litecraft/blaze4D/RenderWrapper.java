package com.github.halotroop.litecraft.blaze4D;

import java.util.ArrayList;
import java.util.List;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;
import io.github.hydos.ginger.engine.elements.GuiTexture;
import io.github.hydos.ginger.engine.elements.objects.Entity;
import io.github.hydos.ginger.engine.elements.objects.Light;
import io.github.hydos.ginger.engine.elements.objects.RenderPlayer;
import io.github.hydos.ginger.engine.font.TextMaster;
import io.github.hydos.ginger.engine.io.Window;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector3f;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector4f;
import io.github.hydos.ginger.engine.particle.ParticleMaster;
import io.github.hydos.ginger.engine.postProcessing.PostProcessing;
import io.github.hydos.ginger.engine.renderEngine.MasterRenderer;
import io.github.hydos.ginger.engine.terrain.Terrain;
import io.github.hydos.ginger.engine.utils.Loader;
import io.github.hydos.ginger.main.GingerMain;

/*
 * The non kid friendly wrapper for your blocky game
 */

public class RenderWrapper {
	
	private static MasterRenderer masterRenderer4D;
		
	public static List<Entity> entities = new ArrayList<Entity>();

	public static List<GuiTexture> guis = new ArrayList<GuiTexture>();

	public static List<Light> lights = new ArrayList<Light>();
	
	public static ThirdPersonCamera camera;
	
	private static final List<Terrain> TERRAIN = new ArrayList<Terrain>();
	private static final List<Entity> NORMAL_ENTITY = new ArrayList<Entity>();
	
	private static String splash = "WILL ADD THIS LATER";
	
	public static void init(String splash, RenderPlayer renderPlayer) {	
		camera = new ThirdPersonCamera(new Vector3f(0,0.1f,0), renderPlayer);
		RenderWrapper.splash = splash;
        Window.setBackgroundColour(0.2f, 0.2f, 0.6f);
        
        masterRenderer4D = new MasterRenderer(camera);
        
        ParticleMaster.init(masterRenderer4D.getProjectionMatrix());
		PostProcessing.init();
	}
	
	public static void cleanup() {
		Window.stop();
		PostProcessing.cleanUp();
		ParticleMaster.cleanUp();
		masterRenderer4D.cleanUp();
		TextMaster.cleanUp();
		Loader.cleanUp();
		System.exit(0);
	}
	
	
	public static void render() {
		Window.update();
		GingerMain.update();
		GingerMain.preRenderScene(masterRenderer4D);
		masterRenderer4D.renderScene(entities, NORMAL_ENTITY, TERRAIN, lights, camera, new Vector4f(0, -1, 0, 100000));
		ParticleMaster.renderParticles(camera);		
		masterRenderer4D.renderGuis(guis);
		TextMaster.render();
		
		Window.swapBuffers();
	}

	public static void preInit() {
		Window.create(2000, 1200, "LiteCraft - " + splash, 60);
		GingerMain.init();
	}
}
