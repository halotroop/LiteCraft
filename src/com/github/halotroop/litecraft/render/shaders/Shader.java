package com.github.halotroop.litecraft.render.shaders;

import java.io.BufferedReader;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;

public abstract class Shader
{
	private int program;
	public Shader()
	{
		program = GL20.glCreateProgram();
		
		if (program == 0)
			noValidMemoryException();
	}
	
	private void addVertexShader(String text)
	{ addProgram(text, GL20.GL_VERTEX_SHADER); }
	private void addFragmentShader(String text)
	{ addProgram(text, GL20.GL_FRAGMENT_SHADER); }
	
	protected void compileShader()
	{
		GL20.glLinkProgram(program);
		
		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0)
			compilationFailedException(program);
		
		GL20.glValidateProgram(program);
		
		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0)
			compilationFailedException(program);
		
		bindAttributes();
	}
	
	public abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName)
	{
		 GL20.glBindAttribLocation(program, attribute, variableName);
	}
	
	public void bind()
	{
		GL20.glUseProgram(program);
	}
	
	public static void unbind()
	{
		GL20.glUseProgram(0);
	}
	
	private void addProgram(String text, int type)
	{
		int shader = GL20.glCreateShader(type);
		
		if (shader == 0)
			noValidMemoryException();
		
		GL20.glShaderSource(shader, text);
		GL20.glCompileShader(shader);
		
		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == 0)
			compilationFailedException(shader);
		
		GL20.glAttachShader(program, shader);
	}
	
	private void noValidMemoryException()
	{
		System.err.println("Shader creation failed! No valid memory!");
		System.exit(1);
	}
	
	private void compilationFailedException(int id)
	{
		System.err.println(GL20.glGetShaderInfoLog(id, 1024));
		System.exit(1);
	}
	
	private static String loadShader(String[] shaderStringArray)
	{
		String source = null;
		for (String line : shaderStringArray)
		{
			source = source + line + "\n";
		}
		
		return source;
	}
}