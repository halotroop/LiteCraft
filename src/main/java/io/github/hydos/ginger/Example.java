package io.github.hydos.ginger;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;
import io.github.hydos.ginger.engine.elements.GuiTexture;
import io.github.hydos.ginger.engine.elements.buttons.TextureButton;
import io.github.hydos.ginger.engine.elements.objects.Entity;
import io.github.hydos.ginger.engine.elements.objects.Light;
import io.github.hydos.ginger.engine.elements.objects.RenderPlayer;
import io.github.hydos.ginger.engine.font.FontType;
import io.github.hydos.ginger.engine.font.GUIText;
import io.github.hydos.ginger.engine.font.TextMaster;
import io.github.hydos.ginger.engine.io.Window;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector2f;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector3f;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector4f;
import io.github.hydos.ginger.engine.obj.ModelLoader;
import io.github.hydos.ginger.engine.obj.normals.NormalMappedObjLoader;
import io.github.hydos.ginger.engine.particle.ParticleMaster;
import io.github.hydos.ginger.engine.particle.ParticleSystem;
import io.github.hydos.ginger.engine.particle.ParticleTexture;
import io.github.hydos.ginger.engine.postProcessing.Fbo;
import io.github.hydos.ginger.engine.postProcessing.PostProcessing;
import io.github.hydos.ginger.engine.renderEngine.MasterRenderer;
import io.github.hydos.ginger.engine.renderEngine.models.TexturedModel;
import io.github.hydos.ginger.engine.renderEngine.texture.ModelTexture;
import io.github.hydos.ginger.engine.renderEngine.tools.MousePicker;
import io.github.hydos.ginger.engine.terrain.Terrain;
import io.github.hydos.ginger.engine.terrain.TerrainTexture;
import io.github.hydos.ginger.engine.terrain.TerrainTexturePack;
import io.github.hydos.ginger.engine.utils.Loader;
import io.github.hydos.ginger.main.GingerMain;
import io.github.hydos.ginger.main.settings.Constants;

public class Example {
	
	private MasterRenderer masterRenderer;
	
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	
	private List<Light> lights = new ArrayList<Light>();
		
	private List<Entity> entities = new ArrayList<Entity>();

	private List<Entity> normalMapEntities = new ArrayList<Entity>();
	
	
	public void main(String[] args) {
		
		
		Window.create(2000, 1200, "Ginger Example", 60);
		
		GingerMain.init();
		
        Window.setBackgroundColour(0.2f, 0.2f, 0.8f);
		
        
		TexturedModel tModel = ModelLoader.loadModel("stall.obj", "stallTexture.png");
		tModel.getTexture().setReflectivity(1f);
		tModel.getTexture().setShineDamper(7f);
		RenderPlayer entity = new RenderPlayer(tModel, new Vector3f(0,0,-3),0,180f,0, new Vector3f(0.2f, 0.2f, 0.2f));
		Constants.movementSpeed = 0.000005f;
		Constants.turnSpeed = 0.00002f;
		Constants.gravity = -0.000000000005f;
		Constants.jumpPower = 0.000005f;
		ThirdPersonCamera camera = new ThirdPersonCamera(new Vector3f(0,0.1f,0), entity);
        masterRenderer = new MasterRenderer(camera);		

        
        FontType font = new FontType(Loader.loadFontAtlas("candara.png"), "candara.fnt");
        
        GUIText text = new GUIText("hi, this is some sample text", 3, font, new Vector2f(0,0), 1f, true);
        text.setColour(0, 1, 0);
        text.setBorderWidth(0.7f);
        text.setBorderEdge(0.4f);
        text.setOffset(new Vector2f(0.003f, 0.003f));
                
        ParticleMaster.init(masterRenderer.getProjectionMatrix());
        
        

		
		TexturedModel dragonMdl = ModelLoader.loadModel("dragon.obj", "stallTexture.png");
		dragonMdl.getTexture().setReflectivity(4f); 
		dragonMdl.getTexture().setShineDamper(2f);
		

		
		Light sun = new Light(new Vector3f(100,105,-100), new Vector3f(1.3f, 1.3f, 1.3f), new Vector3f(0.0001f, 0.0001f, 0.0001f));
		lights.add(sun);
	
		TexturedModel tgrass = ModelLoader.loadModel("grass.obj", "grass.png");
		tgrass.getTexture().setTransparency(true);
		tgrass.getTexture().useFakeLighting(true);
		
		TerrainTexture backgroundTexture = Loader.loadTerrainTexture("grass.png");
		TerrainTexture rTexture = Loader.loadTerrainTexture("mud.png");
		TerrainTexture gTexture = Loader.loadTerrainTexture("grassFlowers.png");
		TerrainTexture bTexture = Loader.loadTerrainTexture("path.png");
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		TerrainTexture blendMap = Loader.loadTerrainTexture("blendMap.png");
		
		Terrain terrain = new Terrain(-0.5f, -0.5f, texturePack, blendMap, "heightmap.png");
		
		Entity dragon = new Entity(dragonMdl, new Vector3f(3,terrain.getHeightOfTerrain(3, -3),-3),0,180f,0, new Vector3f(0.2f, 0.2f, 0.2f));
		
		Entity grassEntity = new Entity(tgrass, new Vector3f(-3,terrain.getHeightOfTerrain(-3, -3),-3),0,180f,0, new Vector3f(0.2f, 0.2f, 0.2f));
		entities.add(grassEntity);
		
		MousePicker picker = new MousePicker(camera, masterRenderer.getProjectionMatrix(), terrain);
		
		
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel.obj"), new ModelTexture("barrel.png"));
		barrelModel.getTexture().setNormalMap(new ModelTexture("modelNormals/barrelNormal.png").getTextureID());
		barrelModel.getTexture().setShineDamper(10f);
		barrelModel.getTexture().setReflectivity(0.5f);
		
		Entity barrel = new Entity(barrelModel, new Vector3f(1,terrain.getHeightOfTerrain(1, 1),1), 0, 0, 0, new Vector3f(0.25f,0.25f,0.25f));
		normalMapEntities.add(barrel);
		entities.add(entity);
		entities.add(dragon);
	
		float colour = 0;
		terrains.add(terrain);
		
		ParticleTexture particleTexture = new ParticleTexture(Loader.loadTexture("particles/smoke.png"), 8);
		
		TextureButton button = new TextureButton("/textures/guis/ginger.png", new Vector2f(0.8f, 0), new Vector2f(0.1f, 0.1f));
		button.show(guis);
		ParticleSystem system = new ParticleSystem(particleTexture, 100, 10f, 0.3f, 4, 3f);
		system.randomizeRotation();
		system.setDirection(new Vector3f(0,0.001f,0), 0.00001f);
		system.setLifeError(0);
		system.setSpeedError(0);
		system.setScaleError(1f);
		
		Fbo fbo = new Fbo(Window.width, Window.height, Fbo.DEPTH_RENDER_BUFFER);
		PostProcessing.init();
		
		while(!Window.closed()) {
			
			if(Window.isUpdating()) {
				Window.update();
				GingerMain.update();
				colour = colour + 0.001f;
				picker.update();
				ParticleMaster.update(camera);
				
				masterRenderer.renderShadowMap(entities, sun);
				
				camera.move();
				entity.move(terrain);
				text.setOutlineColour(new Vector3f(colour, colour /2, colour / 3));
				
				Vector3f terrainPoint = picker.getCurrentTerrainPoint();
				if(terrainPoint!=null) {
					barrel.setPosition(terrainPoint);
					if(Window.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
						normalMapEntities.add(new Entity(barrelModel, terrainPoint, 0, 0, 0, new Vector3f(0.25f,0.25f,0.25f)));
					}
				}
				system.generateParticles(new Vector3f(0,-2,0));

				dragon.increaseRotation(0,1,0);
				barrel.increaseRotation(0, 1, 0);
				
				GingerMain.preRenderScene(masterRenderer);
				
				fbo.bindFrameBuffer();
				masterRenderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));
				ParticleMaster.renderParticles(camera);
				fbo.unbindFrameBuffer();
				PostProcessing.doPostProcessing(fbo.getColourTexture());
//				TODO: get fbo's working
				button.update();
				if(button.isClicked()) {
					System.out.println("click");
					button.hide(guis);
				}
				
				masterRenderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));
				
				masterRenderer.renderGuis(guis);
				TextMaster.render();
				
				Window.swapBuffers();
			}
			
		}
		Window.stop();
		PostProcessing.cleanUp();
		fbo.cleanUp();
		ParticleMaster.cleanUp();
		masterRenderer.cleanUp();
		TextMaster.cleanUp();
		Loader.cleanUp();
		System.exit(0);
		
	}
	
}
