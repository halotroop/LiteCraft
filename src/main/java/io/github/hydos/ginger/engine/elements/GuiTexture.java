package io.github.hydos.ginger.engine.elements;

import io.github.hydos.ginger.engine.mathEngine.vectors.Vector2f;

public class GuiTexture {
	
	private int texture;
	private Vector2f position, scale;
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale) {
		this.texture = texture;
		this.position = position;
		this.scale = scale;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}

	public void setTexture(int texture) {
		this.texture = texture;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public void setScale(Vector2f scale) {
		this.scale = scale;
	}
	
}
