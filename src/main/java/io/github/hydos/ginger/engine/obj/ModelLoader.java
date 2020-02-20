package io.github.hydos.ginger.engine.obj;

import io.github.hydos.ginger.engine.renderEngine.models.TexturedModel;
import io.github.hydos.ginger.engine.renderEngine.texture.ModelTexture;
import io.github.hydos.ginger.engine.utils.Loader;

public class ModelLoader {
	
	public static TexturedModel loadModel(String objPath, String texturePath) {
		ModelData data = OBJFileLoader.loadOBJ(objPath);
		TexturedModel tm = new TexturedModel(Loader.loadToVAO(data.getVertices(), data.getIndices(), data.getNormals(), data.getTextureCoords()), new ModelTexture(texturePath));
		return tm;
	}
	
}
