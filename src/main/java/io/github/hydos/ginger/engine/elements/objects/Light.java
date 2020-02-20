package io.github.hydos.ginger.engine.elements.objects;

import io.github.hydos.ginger.engine.mathEngine.vectors.Vector3f;

public class Light {
	
	private Vector3f position, colour, attenuation;

	public Light(Vector3f position, Vector3f colour) {
		this.position = position;
		this.colour = colour;
	}
	
	public Light(Vector3f position, Vector3f colour, Vector3f attenuation) {
		this.position = position;
		this.colour = colour;
		this.attenuation = attenuation;
	}

	public void setAttenuation(Vector3f a) {
		this.attenuation = a;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getColour() {
		return colour;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setColour(Vector3f colour) {
		this.colour = colour;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}
	
}
