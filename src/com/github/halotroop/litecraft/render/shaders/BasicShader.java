package com.github.halotroop.litecraft.render.shaders;

public class BasicShader extends Shader
{
	public BasicShader()
	{
		super();
	}
	
	@Override
	public void bindAttributes()
	{
		bindAttribute(0, "position");
	}
}