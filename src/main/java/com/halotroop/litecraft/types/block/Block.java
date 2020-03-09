package com.halotroop.litecraft.types.block;

import java.util.*;

import com.github.hydos.ginger.engine.common.obj.ModelLoader;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;

public class Block
{
	public static class Properties
	{ // add properties to this builder!
		private boolean visible = true;
		private boolean fullCube = true;
		private float caveCarveThreshold = -1f; // cannot carve
		private final String identifier;

		public Properties(String identifier)
		{ this.identifier = identifier; }

		public Properties fullCube(boolean fullCube)
		{
			this.fullCube = fullCube;
			return this;
		}

		public Properties visible(boolean visible)
		{
			this.visible = visible;
			return this;
		}

		public Properties caveCarveThreshold(float threshold)
		{
			this.caveCarveThreshold = threshold;
			return this;
		}
	}

	public GLTexturedModel model;
	private final boolean visible, fullCube;
	private final float caveCarveThreshold;
	public final String identifier;
	public String texture;

	public boolean isFullCube()
	{ return this.fullCube; }

	public boolean isVisible()
	{ return this.visible; }

	public float getCaveCarveThreshold()
	{ return this.caveCarveThreshold; }

	protected Block(Properties properties)
	{ this((GLTexturedModel) null, properties); }

	protected Block(String texture, Properties properties)
	{
		this(ModelLoader.loadGenericCube("block/" + texture), properties);
		this.texture = texture;
	}

	protected Block(GLTexturedModel model, Properties properties)
	{
		this.model = model;
		this.visible = properties.visible;
		this.fullCube = properties.fullCube;
		this.identifier = properties.identifier;
		this.caveCarveThreshold = properties.caveCarveThreshold;
		if (model != null)
		{
			this.texture = model.getTexture().getTexture().getLocation();
		}
		else
		{
			this.texture = "DONTLOAD";
		}
		IDENTIFIER_TO_BLOCK.put(this.identifier, this);
		Blocks.blocks.add(this);
	}

	public void updateBlockModelData()
	{
		System.out.println("Updating block with texture at block/" + texture);
		this.model = ModelLoader.loadGenericCube("block/" + texture);
	}

	public static final Block getBlock(String identifier)
	{ return IDENTIFIER_TO_BLOCK.get(identifier); }

	public static final Block getBlockOrAir(String identifier)
	{ return IDENTIFIER_TO_BLOCK.getOrDefault(identifier, Blocks.AIR); }

	private static final Map<String, Block> IDENTIFIER_TO_BLOCK = new HashMap<>();
}
