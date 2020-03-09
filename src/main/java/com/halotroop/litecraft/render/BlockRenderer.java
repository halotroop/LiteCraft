package com.halotroop.litecraft.render;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import com.github.hydos.ginger.engine.common.api.GingerRegister;
import com.github.hydos.ginger.engine.common.elements.objects.RenderObject;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.common.math.Maths;
import com.github.hydos.ginger.engine.opengl.render.Renderer;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;
import com.github.hydos.ginger.engine.opengl.render.shaders.StaticShader;
import com.halotroop.litecraft.types.block.BlockInstance;
import com.halotroop.litecraft.world.gen.WorldGenConstants;

public class BlockRenderer extends Renderer implements WorldGenConstants
{
	public StaticShader shader;
	public int atlasID;

	public BlockRenderer(StaticShader shader, Matrix4f projectionMatrix)
	{
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		this.atlasID = VoxelLoader.createBlockAtlas();
	}

	private void prepBlockInstance(RenderObject entity)
	{
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
			entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	public void prepareModel(GLTexturedModel model)
	{
		GL30.glBindVertexArray(model.getRawModel().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
	}

	public void unbindModel()
	{
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public void enableWireframe()
	{ if (GingerRegister.getInstance().wireframe)
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE); }

	public void disableWireframe()
	{ if (GingerRegister.getInstance().wireframe)
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL); }

	public void prepareRender()
	{
		// TODO: combine VBOS
		shader.start();
		shader.loadSkyColour(Window.getColour());
		shader.loadViewMatrix(GingerRegister.getInstance().game.data.camera);
		shader.loadFakeLightingVariable(true);
		shader.loadShine(1, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlasID);
		enableWireframe();
	}

	public void render(BlockInstance[] renderList)
	{
		prepareRender();
		for (BlockInstance entity : renderList)
		{
			if (entity != null && entity.getModel() != null)
			{
				GLTexturedModel blockModel = entity.getModel();
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, blockModel.getTexture().getTextureID());
				prepBlockInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, blockModel.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
		}
		//		disableWireframe();
		//		shader.stop();
	}
}
