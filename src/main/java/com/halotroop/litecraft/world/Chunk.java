package com.halotroop.litecraft.world;

import java.util.*;
import java.util.function.ToIntFunction;

import org.joml.Vector3f;

import com.halotroop.litecraft.logic.SODSerializable;
import com.halotroop.litecraft.render.BlockRenderer;
import com.halotroop.litecraft.types.block.*;
import com.halotroop.litecraft.world.gen.WorldGenConstants;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import tk.valoeghese.sod.*;

public class Chunk implements BlockAccess, WorldGenConstants, SODSerializable
{
	/** @param x in-chunk x coordinate.
	 * @param  y in-chunk y coordinate.
	 * @param  z in-chunk z coordinate.
	 * @return   creates a long that represents a coordinate, for use as a key in arrays. */
	public static int index(int x, int y, int z)
	{ return (x & MAX_POS) | ((y & MAX_POS) << POS_SHIFT) | ((z & MAX_POS) << DOUBLE_SHIFT); }

	private final Block[] blocks = new Block[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
	private BlockInstance[] blockInstances = new BlockInstance[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
	private boolean shouldRender = false;
	public final int chunkX, chunkY, chunkZ;
	public final int chunkStartX, chunkStartY, chunkStartZ;
	private boolean fullyGenerated = false;
	public final int dimension;
	private boolean dirty = true;
	/** A holder for the rendered blocks in this chunk. This array is *NOT* safe to use for getting BIs at a position!
	 * It can vary in size from 0 to 512 elements long and must only be read linearly. */
	private BlockInstance[] renderedBlocks = new BlockInstance[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];

	public Chunk(World world, int chunkX, int chunkY, int chunkZ, int dimension)
	{
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		this.chunkZ = chunkZ;
		this.chunkStartX = chunkX << POS_SHIFT;
		this.chunkStartY = chunkY << POS_SHIFT;
		this.chunkStartZ = chunkZ << POS_SHIFT;
		this.dimension = dimension;
	}

	public boolean doRender()
	{ return this.shouldRender; }

	public void setFullyGenerated(boolean fullyGenerated)
	{ this.fullyGenerated = fullyGenerated; }

	@Override
	public Block getBlock(int x, int y, int z)
	{
		if (x > CHUNK_SIZE || y > CHUNK_SIZE || z > CHUNK_SIZE || x < 0 || y < 0 || z < 0)
		{ throw new RuntimeException("Block [" + x + ", " + y + ", " + z + "] out of chunk bounds!"); }
		return blocks[index(x, y, z)];
	}

	public BlockInstance getBlockInstance(int x, int y, int z)
	{
		if (x > CHUNK_SIZE || y > CHUNK_SIZE || z > CHUNK_SIZE || x < 0 || y < 0 || z < 0)
		{ throw new RuntimeException("BlockInstance [" + x + ", " + y + ", " + z + "] out of chunk bounds!"); }
		return this.blockInstances[index(x, y, z)];
	}

	public void render(BlockRenderer blockRenderer)
	{
		if (shouldRender)
		{
			if (dirty)
			{
				dirty = false;
				List<BlockInstance> tempList = new ArrayList<>();
				Arrays.fill(renderedBlocks, null);
				for (int x = 0; x < CHUNK_SIZE; x++)
					for (int y = 0; y < CHUNK_SIZE; y++)
						for (int z = 0; z < CHUNK_SIZE; z++)
				{
					BlockInstance block = getBlockInstance(x, y, z);
					// Check for chunk edges to avoid errors when get the neighboring blocks, TODO fix this
					if (x == 0 || x == CHUNK_SIZE - 1 || z == 0 || z == CHUNK_SIZE - 1 || y == 0 || y == CHUNK_SIZE - 1)
					{
						tempList.add(block);
						continue;
					}
					// Check for air. Yes this is stupid, TODO fix this too
					try
					{
						if (getBlockInstance(x - 1, y, z).getModel() == null || getBlockInstance(x + 1, y, z).getModel() == null ||
							getBlockInstance(x, y - 1, z).getModel() == null || getBlockInstance(x, y + 1, z).getModel() == null ||
							getBlockInstance(x, y, z - 1).getModel() == null || getBlockInstance(x, y, z + 1).getModel() == null)
						{ tempList.add(block); }
					}
					catch (NullPointerException e)
					{ // this seems to be a hotspot for errors
						e.printStackTrace(); // so I can add a debug breakpoint on this line
						throw e; // e
					}
				}
				renderedBlocks = tempList.toArray(BlockInstance[]::new);
			}
			blockRenderer.prepareRender();
			blockRenderer.render(renderedBlocks);
			blockRenderer.shader.stop();
		}
	}

	/** Change the block in this exact position
	 * 
	 * @param x,    y, z The coordinate position of block to overwrite
	 * @param block The block to place there */
	@Override
	public void setBlock(int x, int y, int z, Block block)
	{
		// This section makes sure the blocks don't go out of range
		if (x > MAX_POS)
			x = MAX_POS;
		else if (x < 0) x = 0;
		if (y > MAX_POS)
			y = MAX_POS;
		else if (y < 0) y = 0;
		if (z > MAX_POS)
			z = MAX_POS;
		else if (z < 0) z = 0;
		//
		this.blocks[index(x, y, z)] = block;
		if (this.shouldRender) this.blockInstances[index(x, y, z)] = new BlockInstance(block, new Vector3f(this.chunkStartX + x, this.chunkStartY + y, this.chunkStartZ + z));
		dirty = true;
	}

	/** Set whether or not the chunk should render */
	public void setRender(boolean render)
	{
		if (render && !this.shouldRender) // if it has been changed to true
			for (int x = 0; x < CHUNK_SIZE; ++x)
				for (int y = 0; y < CHUNK_SIZE; ++y)
					for (int z = 0; z < CHUNK_SIZE; ++z)
			{
				Block block = this.blocks[index(x, y, z)];
				this.blockInstances[index(x, y, z)] = new BlockInstance(block,
					new Vector3f(
						this.chunkStartX + x,
						this.chunkStartY + y,
						this.chunkStartZ + z));
			}
		else if (!render && this.shouldRender) // else if it has been changed to false.
			// we need to check both variables because there are two cases that make
			// the if statement fall to here
			blockInstances = new BlockInstance[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
		this.shouldRender = render;
		dirty = true;
	}

	public boolean isFullyGenerated()
	{ return this.fullyGenerated; }

	@Override
	public void read(BinaryData data)
	{
		Int2ObjectMap<Block> palette = new Int2ObjectArrayMap<>();
		DataSection paletteData = data.get("palette");
		boolean readInt = true; // whether the thing from the palette to be read is int
		int intIdCache = 0;
		//
		for (Object o : paletteData)
		{
			if (readInt)
			{
				intIdCache = (int) o;
				readInt = false;
			}
			else
			{
				palette.put(intIdCache, Block.getBlockOrAir((String) o));
				readInt = true;
			}
		}
		//
		IntArrayDataSection blockData = data.getIntArray("block");
		int index = 0;
		// Iterate over each block in the chunk
		for (int z = 0; z < CHUNK_SIZE; ++z) // z, y, x order for data saving and loading so we can use incremental pos hashes
			for (int y = 0; y < CHUNK_SIZE; ++y)
				for (int x = 0; x < CHUNK_SIZE; ++x)
		{
			blocks[index] = palette.get(blockData.readInt(index));
			++index;
		}
		//
		DataSection properties = data.get("properties");
		try
		{
			this.fullyGenerated = properties.readBoolean(0); // index 0 is the "fully generated" property
		}
		catch (Throwable e)
		{
			if (!readExceptionNotif)
			{
				System.out.println("An exception occurred reading properties for a chunk! This could be a benign error due to updates to chunk properties.");
				readExceptionNotif = true;
			}
		}
	}

	private static boolean readExceptionNotif = false;
	private int nextId; // for saving

	@Override
	public void write(BinaryData data)
	{
		Object2IntMap<Block> palette = new Object2IntArrayMap<>(); // block to int id
		DataSection paletteData = new DataSection();
		IntArrayDataSection blockData = new IntArrayDataSection();
		int index = 0;
		nextId = 0;
		ToIntFunction<Block> nextIdProvider = b -> nextId++;
		//
		for (int z = 0; z < CHUNK_SIZE; ++z) // z, y, x order for data saving and loading so we can use incremental pos hashes
			for (int y = 0; y < CHUNK_SIZE; ++y)
				for (int x = 0; x < CHUNK_SIZE; ++x)
		{
			Block b = blocks[index];
			blockData.writeInt(palette.computeIntIfAbsent(b, nextIdProvider));
			++index;
		}
		//
		palette.forEach((b, id) ->
		{
			paletteData.writeInt(id);
			paletteData.writeString(b.identifier);
		});
		//
		data.put("palette", paletteData);
		data.put("block", blockData);
		//
		DataSection properties = new DataSection();
		properties.writeBoolean(this.fullyGenerated);
		data.put("properties", properties);
		dirty = true;
	}
}
