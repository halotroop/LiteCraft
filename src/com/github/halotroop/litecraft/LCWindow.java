package com.github.halotroop.litecraft;

import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import com.bwyap.engine.EngineInterface;
import com.bwyap.engine.input.InputHandler;
import com.bwyap.engine.window.WindowInterface;

public class LCWindow extends com.bwyap.engine.window.Window
{
	private long windowLong;

	public long getWindowLong()
	{ return windowLong; }

	public void setWindowLong(long window)
	{ this.windowLong = window; }

	public LCWindow(int width, int height)
	{
		super(width, height, "LiteCraft", true);
		
		init();
		start();
	}

	@Override
	public boolean shouldClose()
	{
		return false;
	}

	@Override
	public void processEvents()
	{
		
	}

	@Override
	public void swapDisplayBuffers()
	{
		
	}

	@Override
	public EngineInterface createEngine() throws Exception
	{
		return null;
	}

	@Override
	public void init()
	{
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 1);
		
		// Create the window
		windowLong = GLFW.glfwCreateWindow(getWidth(), getHeight(), getDefaultTitle(), 0, 0);
		if (windowLong == 0) throw new RuntimeException("Failed to create the GLFW window");
	}

	@Override
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

	@Override
	public void dispose()
	{
		Callbacks.glfwFreeCallbacks(windowLong);
		GLFW.glfwDestroyWindow(windowLong);
	}

	@Override
	protected void setWindowTitle(String title)
	{
		super.setDefaultTitle(title);
	}

}