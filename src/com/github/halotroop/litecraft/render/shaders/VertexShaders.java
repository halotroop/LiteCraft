package com.github.halotroop.litecraft.render.shaders;

public class VertexShaders
{
	public static String[] basicVertex =
	{
		"#version 400",
		"layout (location = 0) in vec3 position",
		"void main() {",
		"gl_Position = vec4(postion, 1.0);"
	};
}
