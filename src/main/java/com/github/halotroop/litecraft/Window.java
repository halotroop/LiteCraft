package com.github.halotroop.litecraft;

import java.nio.IntBuffer;

import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

import com.github.halotroop.litecraft.input.*;

public class Window
{
	private long windowLong = 0;
	public long getWindowId() { return windowLong; }

	private String title;
	public String getWindowTitle() { return title; }
	protected void setWindowTitle(String title)
	{
		this.title = title;
		if (windowLong != 0)
			GLFW.glfwSetWindowTitle(windowLong, title);
	}

	private int width, height;
	public int getHeight()
	{ return height; }
	public int getWidth()
	{ return width; }
	public void setHeight(int height)
	{
		this.height = height;
		if (windowLong != 0)
			GLFW.glfwSetWindowSize(windowLong, getWidth(), getHeight());
	}
	public void setWidth(int width)
	{
		this.width = width;
		if (windowLong != 0)
			GLFW.glfwSetWindowSize(windowLong, getWidth(), getHeight());
	}
	public void setWidthAndHeight(int width, int height)
	{
		this.width = width;
		this.height = height;
		if (windowLong != 0)
			GLFW.glfwSetWindowSize(windowLong, getWidth(), getHeight());
	}

	public boolean shouldClose()
	{ return GLFW.glfwWindowShouldClose(windowLong); }

	public void closeWindow()
	{ GLFW.glfwSetWindowShouldClose(windowLong, true); }

	// (Always useful to have simpler inputs, even if you only ever plan on using these once. Can be great for debugging, or just making life easier.
	public Window()
	{ this(640, 480); }

	public Window(int width, int height)
	{ this(width, height, "LiteCraft"); }

	public Window(int width, int height, String title)
	{
		// Keep these in this order!
		setWindowTitle(title);
		setWidthAndHeight(width, height);
		init();
		// Thank you.
	}

	private void init()
	{
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 1);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, 1);
		// Create the window
		this.windowLong = GLFW.glfwCreateWindow(getWidth(), getHeight(), getWindowTitle(), 0, 0);
		if (windowLong == 0) throw new RuntimeException("Failed to create the GLFW window");
		// Get the thread stack and push a new frame
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(windowLong, pWidth, pHeight);
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			GLFW.glfwSetWindowPos(windowLong, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
			GLFW.glfwMakeContextCurrent(windowLong);
			GLFW.glfwShowWindow(windowLong);
			createCallbacks();
		}
	}

	private void createCallbacks()
	{
		KeyCallbackHandler.trackWindow(windowLong);
		MouseCallbackHandler.trackWindow(windowLong);
	}
	
	public void destroy()
	{
		Callbacks.glfwFreeCallbacks(windowLong);
		GLFW.glfwDestroyWindow(windowLong);
	}

	public void render()
	{ swapDisplayBuffers(); }

	public void hide()
	{ GLFW.glfwHideWindow(windowLong); }

	public void show()
	{ GLFW.glfwShowWindow(windowLong); }

	private void swapDisplayBuffers()
	{ GLFW.glfwSwapBuffers(windowLong); }
}
