package com.github.halotroop.litecraft.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import com.github.halotroop.litecraft.render.model.Model;
import com.github.halotroop.litecraft.render.model.Vertex;
import com.github.halotroop.litecraft.render.shaders.BasicShader;
import com.github.halotroop.litecraft.render.shaders.Shader;

public class Renderer
{
	private BasicShader basicShader;
	private Model model;
	public Renderer()
	{
		init();
		
		basicShader = new BasicShader();
		
		model = new Model();
		
		Vertex[] vertices =
		{
			new Vertex(-1, -1, 0),
			new Vertex(1, -1, 0),
			new Vertex(0, 1, 0)
		};
		
		model.bufferVertices(vertices);
	}
	
	public void render()
	{
		basicShader.bind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, model.getVBO());
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, Vertex.SIZE * 4, 0);
		GL20.glDrawArrays(GL20.GL_TRIANGLES, 0, model.getSize());
		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		Shader.unbind();
	}

	private void init()
	{
		prepare();
	}
	
	private void prepare()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);	// Set the background color
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}
	
	
	public void cleanUp()
	{
		
	}
}
