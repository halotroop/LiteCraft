package io.github.hydos.ginger.engine.render.renderers;

import java.util.*;

import org.joml.*;
import org.lwjgl.opengl.*;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;
import io.github.hydos.ginger.engine.elements.objects.*;
import io.github.hydos.ginger.engine.io.Window;
import io.github.hydos.ginger.engine.math.Maths;
import io.github.hydos.ginger.engine.render.MasterRenderer;
import io.github.hydos.ginger.engine.render.models.*;
import io.github.hydos.ginger.engine.render.shaders.NormalMappingShader;
import io.github.hydos.ginger.engine.render.texture.ModelTexture;

public class NormalMappingRenderer
{
	private NormalMappingShader shader;

	public NormalMappingRenderer(Matrix4f projectionMatrix)
	{
		this.shader = new NormalMappingShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(Map<TexturedModel, List<Entity>> entities, Vector4f clipPlane, List<Light> lights, ThirdPersonCamera camera)
	{
		shader.start();
		prepare(clipPlane, lights, camera);
		for (TexturedModel model : entities.keySet())
		{
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch)
			{
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();
		entities.clear();
	}

	public void cleanUp()
	{ shader.cleanUp(); }

	private void prepareTexturedModel(TexturedModel model)
	{
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		ModelTexture texture = model.getTexture();
		if (texture.isTransparent())
		{ MasterRenderer.disableCulling(); }
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
	}

	private void unbindTexturedModel()
	{
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
			entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(0, 0);
	}

	private void prepare(Vector4f clipPlane, List<Light> lights, ThirdPersonCamera camera)
	{
		shader.loadClipPlane(clipPlane);
		//need to be public variables in MasterRenderer
		shader.loadSkyColour(Window.getColour());
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		shader.loadLights(lights, viewMatrix);
		shader.loadViewMatrix(viewMatrix);
	}
}
