package io.github.hydos.ginger.engine.cameras;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

import io.github.hydos.ginger.engine.elements.objects.RenderPlayer;
import io.github.hydos.ginger.engine.io.Window;
import io.github.hydos.ginger.engine.mathEngine.vectors.Vector3f;

public class ThirdPersonCamera {

	private float distanceFromPlayer = 5;
	private float angleAroundPlayer = 0;


	private Vector3f position = new Vector3f(0,0,0);
	private float pitch, yaw;
	private float roll;


	private RenderPlayer player;


	public ThirdPersonCamera(RenderPlayer player) {
		this.player = player;

	}

	public ThirdPersonCamera(Vector3f vector3f, RenderPlayer player) {
		this.position = vector3f;
		this.player = player;

	}

	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
	}
	
	public void invertPitch(){
		this.pitch = -pitch;
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
	
	private void calculateCameraPosition(float horizDistance, float verticDistance){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance;
	}
	
	private float calculateHorizontalDistance(){
		float hD = (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
		if(hD < 0)
			hD = 0;
		return hD;
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch+4)));
	}
	
	private void calculateZoom(){
		GLFW.glfwSetScrollCallback(Window.window, new GLFWScrollCallback() {
			@Override public void invoke (long win, double dx, double dy) {
				float zoomLevel = (float) dy * 0.1f;
				distanceFromPlayer -= zoomLevel;
			}
		});	
	}
	
	private void calculatePitch(){
		if(Window.isMouseDown(1)) {
			float pitchChange = (float) (Window.dy * 0.2f);
			pitch += pitchChange;
			if(pitch < 0){
				pitch = 0;
			}else if(pitch > 90){
				pitch = 90;
			}
		}
	}
	
	private void calculateAngleAroundPlayer(){
		if(Window.isMouseDown(1)) {
			float angleChange = (float) (Window.dx * 0.3f);
			angleAroundPlayer -= angleChange;
		}
	}
	

}
