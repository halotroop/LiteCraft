package com.github.halotroop.litecraft;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import com.github.halotroop.litecraft.types.gui.MainMenu;

public class LCWindow
{
	private long windowLong;
	public long getWindowLong() { return windowLong; }
	
	private String title;
	protected void setWindowTitle(String title) {	this.title = title;	}
	public String getWindowTitle() {	return title;	}
	
	private int width, height;
	public int getWidth() {	return width;	}
	public int getHeight() {	return height;	}
	public void setWidth(int width) {	this.width = width;	}
	public void setHeight(int height) {	this.height = height;	}
	public void setWidthAndHeight(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public LCWindow(int width, int height, String title)
	{
		setWindowTitle(title);
		setWidthAndHeight(width, height);
		
		init();
		start();
	}

	public LCWindow(int width, int height)
	{
		this(width, height, "LiteCraft");
	}
	
	public boolean shouldClose()
	{
		return false;
	}

	public void swapDisplayBuffers()
	{
		
	}

	public void init()
	{
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 1);
		
		// Create the window
		this.windowLong = GLFW.glfwCreateWindow(getWidth(), getHeight(), getWindowTitle(), 0, 0);
		if (windowLong == 0) throw new RuntimeException("Failed to create the GLFW window");
	}

	public void start()
	{
		// Get the thread stack and push a new frame
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			// Get the windowLong size passed to glfwCreateWindow
			GLFW.glfwGetWindowSize(windowLong, pWidth, pHeight);
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			// Center the window
			GLFW.glfwSetWindowPos(windowLong, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
			// Make the OpenGL context current
			GLFW.glfwMakeContextCurrent(windowLong);
			// Enable v-sync
			GLFW.glfwSwapInterval(1);
			// Make the window visible
			GLFW.glfwShowWindow(windowLong);
		}
	}

	public void dispose()
	{
		Callbacks.glfwFreeCallbacks(windowLong);
		GLFW.glfwDestroyWindow(windowLong);
	}
}