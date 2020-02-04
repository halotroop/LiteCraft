package com.github.halotroop.litecraft.render.model;

import org.joml.Vector3i;

public class Vertex extends Vector3i
{
	public static final int SIZE = 3;

	public Vertex(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
