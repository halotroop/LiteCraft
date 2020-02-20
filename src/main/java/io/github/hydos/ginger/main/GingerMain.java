package io.github.hydos.ginger.main;

import io.github.hydos.ginger.UI.UIManager;
import io.github.hydos.ginger.UI.enums.UIColourType;
import io.github.hydos.ginger.engine.font.TextMaster;
import io.github.hydos.ginger.engine.obj.ModelLoader;
import io.github.hydos.ginger.engine.obj.normals.NormalMappedObjLoader;
import io.github.hydos.ginger.engine.renderEngine.MasterRenderer;
import io.github.hydos.ginger.engine.renderEngine.models.RawModel;
import io.github.hydos.ginger.engine.renderEngine.models.TexturedModel;
import io.github.hydos.ginger.engine.renderEngine.texture.ModelTexture;

public class GingerMain {
	
	public static UIManager manager;
	
	public static void init() {
        TextMaster.init();
        manager = new UIManager(UIColourType.dark);
	}
	
	public static TexturedModel createTexturedModel(String texturePath, String modelPath) {
		TexturedModel model = ModelLoader.loadModel(modelPath, texturePath);
		return model;
	}
	
	public static TexturedModel createTexturedModel(String texturePath, String modelPath, String normalMapPath) {
		RawModel model = NormalMappedObjLoader.loadOBJ(modelPath);
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(texturePath));
		return texturedModel;
	}
	
	public static void update() {
		manager.update();
	}
	
	public static void preRenderScene(MasterRenderer renderer) {
		renderer.renderGui(manager.getBackgroundTexture());
	}
}
