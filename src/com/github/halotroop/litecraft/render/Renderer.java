package com.github.halotroop.litecraft.render;

import org.lwjgl.opengl.GL11;

public class Renderer
{
	public Renderer()
	{
		init();
	}
	
	public void render()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}

	private void init()
	{
		prepare();
	}
	
	private void prepare()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);	// Set the background color
	}
	
	
	public void cleanUp()
	{
		
	}
}
