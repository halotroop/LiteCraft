package io.github.hydos.ginger.engine.cameras;

import org.lwjgl.glfw.GLFW;

import io.github.hydos.ginger.engine.io.Window;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector3f;

public class FirstPersonCamera {
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch, yaw;
	private float roll;
	
	
	public FirstPersonCamera() {
		
		
	}
	
	public FirstPersonCamera(Vector3f vector3f) {
		this.position = vector3f;
	}

	public void move() {
		if(Window.isKeyDown(GLFW.GLFW_KEY_W)){
			position.z-=0.05f;
		}
		if(Window.isKeyDown(GLFW.GLFW_KEY_A)){
			position.x-=0.05f;
		}
		if(Window.isKeyDown(GLFW.GLFW_KEY_S)){
			position.z-=-0.05f;
		}
		if(Window.isKeyDown(GLFW.GLFW_KEY_D)){
			position.x+=0.05f;
		}
		if(Window.isKeyDown(GLFW.GLFW_KEY_SPACE)){
			position.y+=0.05f;
		}
		if(Window.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)){
			position.y-=0.05f;
		}
		
		if(Window.isKeyDown(GLFW.GLFW_KEY_LEFT)){
			yaw-=0.5f;
		}
		if(Window.isKeyDown(GLFW.GLFW_KEY_RIGHT)){
			yaw+=0.5f;
		}
		
		
	}

	public Vector3f getPosition() {
		return position;
	}


	public float getPitch() {
		return pitch;
	}


	public float getYaw() {
		return yaw;
	}


	public float getRoll() {
		return roll;
	}
	
}
