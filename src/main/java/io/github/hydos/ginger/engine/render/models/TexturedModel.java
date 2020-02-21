package io.github.hydos.ginger.engine.render.models;

import io.github.hydos.ginger.engine.render.texture.ModelTexture;

public class TexturedModel
{
	private RawModel rawModel;
	private ModelTexture texture;

	public TexturedModel(RawModel model, ModelTexture texture)
	{
		this.rawModel = model;
		this.texture = texture;
	}

	public RawModel getRawModel()
	{ return rawModel; }

	public ModelTexture getTexture()
	{ return texture; }
}
