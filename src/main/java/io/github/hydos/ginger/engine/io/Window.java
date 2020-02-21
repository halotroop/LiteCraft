package io.github.hydos.ginger.engine.io;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import com.github.halotroop.litecraft.LiteCraftMain;

import io.github.hydos.ginger.engine.render.texture.Image;

public class Window
{
	private static String title;
	public static long window;
	private static Vector3f backgroundColour = new Vector3f(0.2f, 0.2f, 0.6f);
	private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
	private static boolean[] mouseButtons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
	private static GLFWImage.Buffer iconBuffer = null;
	private static double fpsCap, time, processedTime = 0;
	public static double dy = 0;
	public static double dx = 0;
	static double oldX = 0;
	static double oldY = 0;
	static double newX = 0;
	static double newY = 0;
	public static GLCapabilities glContext;
	public static int actuallWidth, actuallHeight;

	public static void create(int width, int height, String title, int fps)
	{
		Window.actuallHeight = height;
		Window.actuallWidth = width;
		Window.title = title;
		fpsCap = fps;
		create();
	}

	public static void create()
	{
		if (!GLFW.glfwInit())
		{
			System.err.println("Error: Couldn't initialize GLFW");
			System.exit(-1);
		}
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		window = GLFW.glfwCreateWindow(actuallWidth, actuallHeight, title, (LiteCraftMain.fullscreen) ? GLFW.glfwGetPrimaryMonitor() : 0, window);
		if (window == 0)
		{
			System.err.println("Error: Couldnt initilize window");
			System.exit(-1);
		}
		GLFW.glfwMakeContextCurrent(window);
		glContext = GL.createCapabilities();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GLFW.glfwSetWindowPos(window, (vidmode.width() - LiteCraftMain.width) / 2, (vidmode.height() - LiteCraftMain.height) / 2);
		GLFW.glfwShowWindow(window);
		time = getTime();
		getCurrentTime();
	}

	public static boolean closed()
	{ return GLFW.glfwWindowShouldClose(window); }

	public static void update()
	{
		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(window, widthBuffer, heightBuffer);
		GL11.glViewport(0, 0, LiteCraftMain.width, LiteCraftMain.height);
		GL11.glClearColor(backgroundColour.x, backgroundColour.y, backgroundColour.z, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GLFW.glfwPollEvents();
		newX = Window.getMouseX();
		newY = Window.getMouseY();
		Window.dx = newX - oldX;
		Window.dy = newY - oldY;
		oldX = newX;
		oldY = newY;
	}

	public static void swapBuffers()
	{ GLFW.glfwSwapBuffers(window); }

	public static boolean isKeyDown(int keycode)
	{ return GLFW.glfwGetKey(window, keycode) == 1; }

	public static boolean isMouseDown(int mouseButton)
	{ return GLFW.glfwGetMouseButton(window, mouseButton) == 1; }

	public static boolean isKeyPressed(int keyCode)
	{ return isKeyDown(keyCode) && !keys[keyCode]; }

	public static boolean isKeyReleased(int keyCode)
	{ return !isKeyDown(keyCode) && keys[keyCode]; }

	public static boolean isMousePressed(int keyCode)
	{ return isMouseDown(keyCode) && !mouseButtons[keyCode]; }

	public static boolean isMouseReleased(int keyCode)
	{ return !isMouseDown(keyCode) && mouseButtons[keyCode]; }

	public static double getMouseX()
	{
		DoubleBuffer buffer = BufferUtils.createDoubleBuffer(1);
		GLFW.glfwGetCursorPos(window, buffer, null);
		return buffer.get(0);
	}

	public static double getMouseY()
	{
		DoubleBuffer buffer = BufferUtils.createDoubleBuffer(1);
		GLFW.glfwGetCursorPos(window, null, buffer);
		return buffer.get(0);
	}

	public static double getTime()
	{
		double f = (double) System.nanoTime() / (double) 1000000000;
		return f;
	}

	public static float getFloatTime()
	{
		float f = (System.nanoTime() / (float) 1000000000);
		return f;
	}

	public static boolean isUpdating()
	{
		double nextTime = getTime();
		double passedTime = nextTime - time;
		processedTime += passedTime;
		time = nextTime;
		while (processedTime > 1.0 / fpsCap)
		{
			processedTime -= 1.0 / fpsCap;
			return true;
		}
		return false;
	}

	public static void stop()
	{ GLFW.glfwTerminate(); }

	public static void setBackgroundColour(float r, float g, float b)
	{ backgroundColour = new Vector3f(r, g, b); }

	public static void setIcon(String path)
	{
		Image icon = Image.createImage("/res/textures/" + path);
		GLFWImage iconImage = GLFWImage.malloc();
		iconBuffer = GLFWImage.malloc(1);
		iconImage.set(icon.getWidth(), icon.getHeight(), icon.getImage());
		iconBuffer.put(0, iconImage);
	}

	public static void showIcon()
	{
		if (iconBuffer != null)
		{ GLFW.glfwSetWindowIcon(window, iconBuffer); }
	}

	public static void lockMouse()
	{ GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED); }

	public static void unlockMouse()
	{ GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL); }

	public static Vector3f getColour()
	{ return Window.backgroundColour; }

	private static long getCurrentTime()
	{ return (long) (GLFW.glfwGetTime() * 1000 / GLFW.glfwGetTimerFrequency()); }

	public static Vector2f getNormalizedMouseCoordinates()
	{
		float normalX = -1.0f + 2.0f * (float) getMouseX() / LiteCraftMain.width;
		float normalY = 1.0f - 2.0f * (float) getMouseY() / LiteCraftMain.height;
		return new Vector2f(normalX, normalY);
	}
}
