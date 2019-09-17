package com.github.halotroop.litecraft;

import java.nio.IntBuffer;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

public class Window {

	private long window;

	public long getWindow() {
		return window;
	}

	public void setWindow(long window) {
		this.window = window;
	}

	public Window()
	{
		// Setup an error callback. The default implementation
				// will print the error message in System.err.
				GLFWErrorCallback.createPrint(System.err).set();

				// Initialize GLFW. Most GLFW functions will not work before doing this.
				if ( !GLFW.glfwInit() )
					throw new IllegalStateException("Unable to initialize GLFW");

				// Configure GLFW
				GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
				GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
				GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable

				// Create the window
				
				window = GLFW.glfwCreateWindow(1600, 900, "Hello World!", 0, 0);
				if ( window == 0 )
					throw new RuntimeException("Failed to create the GLFW window");

				// Setup a key callback. It will be called every time a key is pressed, repeated or released.
				GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
					if ( key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE )
						GLFW.glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
				});

				// Get the thread stack and push a new frame
				try ( MemoryStack stack = MemoryStack.stackPush() ) {
					IntBuffer pWidth = stack.mallocInt(1); // int*
					IntBuffer pHeight = stack.mallocInt(1); // int*

					// Get the window size passed to glfwCreateWindow
					GLFW.glfwGetWindowSize(window, pWidth, pHeight);

					// Get the resolution of the primary monitor
					GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

					// Center the window
					GLFW.glfwSetWindowPos(
						window,
						(vidmode.width() - pWidth.get(0)) / 2,
						(vidmode.height() - pHeight.get(0)) / 2
					);
				} // the stack frame is popped automatically

				// Make the OpenGL context current
				GLFW.glfwMakeContextCurrent(window);
				// Enable v-sync
				GLFW.glfwSwapInterval(1);

				// Make the window visible
				GLFW.glfwShowWindow(window);
			}

	
	}
