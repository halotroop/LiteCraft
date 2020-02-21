package io.github.hydos.ginger.engine.render.renderers;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import io.github.hydos.ginger.engine.cameras.ThirdPersonCamera;
import io.github.hydos.ginger.engine.render.models.RawModel;
import io.github.hydos.ginger.engine.render.shaders.SkyboxShader;
import io.github.hydos.ginger.engine.utils.Loader;

public class SkyboxRenderer
{
	private static final float SIZE = 50f;
	private static final float[] VERTICES =
	{
		-SIZE, SIZE, -SIZE,
		-SIZE, -SIZE, -SIZE,
		SIZE, -SIZE, -SIZE,
		SIZE, -SIZE, -SIZE,
		SIZE, SIZE, -SIZE,
		-SIZE, SIZE, -SIZE,
		-SIZE, -SIZE, SIZE,
		-SIZE, -SIZE, -SIZE,
		-SIZE, SIZE, -SIZE,
		-SIZE, SIZE, -SIZE,
		-SIZE, SIZE, SIZE,
		-SIZE, -SIZE, SIZE,
		SIZE, -SIZE, -SIZE,
		SIZE, -SIZE, SIZE,
		SIZE, SIZE, SIZE,
		SIZE, SIZE, SIZE,
		SIZE, SIZE, -SIZE,
		SIZE, -SIZE, -SIZE,
		-SIZE, -SIZE, SIZE,
		-SIZE, SIZE, SIZE,
		SIZE, SIZE, SIZE,
		SIZE, SIZE, SIZE,
		SIZE, -SIZE, SIZE,
		-SIZE, -SIZE, SIZE,
		-SIZE, SIZE, -SIZE,
		SIZE, SIZE, -SIZE,
		SIZE, SIZE, SIZE,
		SIZE, SIZE, SIZE,
		-SIZE, SIZE, SIZE,
		-SIZE, SIZE, -SIZE,
		-SIZE, -SIZE, -SIZE,
		-SIZE, -SIZE, SIZE,
		SIZE, -SIZE, -SIZE,
		SIZE, -SIZE, -SIZE,
		-SIZE, -SIZE, SIZE,
		SIZE, -SIZE, SIZE
	};
	private static String[] TEXTURE_FILES =
	{
		"right.png", "left.png", "up.png", "down.png", "back.png", "front.png"
	};
	private RawModel cube;
	private int texture;
	private SkyboxShader shader;

	public SkyboxRenderer(Matrix4f projectionMatrix)
	{
		cube = Loader.loadToVAO(VERTICES, 3);
		texture = Loader.loadCubeMap(TEXTURE_FILES);
		shader = new SkyboxShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(ThirdPersonCamera camera)
	{
		shader.start();
		shader.loadViewMatrix(camera);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public void cleanUp()
	{ shader.cleanUp(); }
}
