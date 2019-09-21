package com.github.halotroop.litecraft.render.model;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class Model
{
	private int vbo, size;
	
	public Model()
	{
		vbo = GL15.glGenBuffers();
		
		size = 0;
	}
	
	public void bufferVertices(Vertex[] verts)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(verts.length * Vertex.SIZE);
		
		for (Vertex vertex : verts)
		{
			buffer.put(vertex.x);
			buffer.put(vertex.y);
			buffer.put(vertex.z);
		}
		
		buffer.flip();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		size = verts.length;
	}

	public int getVBO()
	{ return vbo; }

	public int getSize()
	{ return size; }
}
