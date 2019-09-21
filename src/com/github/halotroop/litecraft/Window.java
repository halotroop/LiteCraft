package com.github.halotroop.litecraft;

import java.nio.IntBuffer;

import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryStack;

public class Window
{
	private long windowLong = 0;

	public long getWindowLong()
	{
		return windowLong;
	}

	private String title;

	public String getWindowTitle()
	{
		return title;
	}

	protected void setWindowTitle(String title)
	{
		this.title = title;
		if (windowLong != 0)
			GLFW.glfwSetWindowTitle(windowLong, title);
	}

	private int width, height;

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

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
	{
		return GLFW.glfwWindowShouldClose(windowLong);
	}

	private void closeWindow()
	{
		GLFW.glfwSetWindowShouldClose(windowLong, true);
	}

	// (Always useful to have simpler inputs, even if you only ever plan on using these once. Can be great for debugging, or just making life easier.
	public Window()
	{this(1600, 900);}
	public Window(int width, int height)
	{this(width, height, "LiteCraft");}
	public Window(int width, int height, String title)
	{
		// Keep these in this order!
		setWindowTitle(title);
		setWidthAndHeight(width, height);
		init();
		// Thank you.
	}

	public void init()
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
			GLFW.glfwGetWindowSize(windowLong, pWidth, pHeight); // Get the windowLong size passed to glfwCreateWindow
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()); // Get the resolution of the primary monitor
			GLFW.glfwSetWindowPos(windowLong, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2); // Center the window
			GLFW.glfwMakeContextCurrent(windowLong); // Make the OpenGL context current
			GLFW.glfwSwapInterval(1); // Enable V-Sync
			GLFW.glfwShowWindow(windowLong); // Make the window visible
		}
	}
	
	public void destroy()
	{
		Callbacks.glfwFreeCallbacks(windowLong);
		GLFW.glfwDestroyWindow(windowLong);
	}

	public void render()
	{
		swapDisplayBuffers();
	}

	public void hide()
	{
		GLFW.glfwHideWindow(windowLong);
	}

	public void show()
	{
		GLFW.glfwShowWindow(windowLong);
	}

	private void swapDisplayBuffers()
	{
		GLFW.glfwSwapBuffers(windowLong);
	}
	
}
