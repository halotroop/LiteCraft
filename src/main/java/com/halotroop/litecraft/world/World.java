package com.halotroop.litecraft.world;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongConsumer;

import org.joml.Vector3f;

import com.halotroop.litecraft.Litecraft;
import com.halotroop.litecraft.render.BlockRenderer;
import com.halotroop.litecraft.save.LitecraftSave;
import com.halotroop.litecraft.types.block.*;
import com.halotroop.litecraft.types.entity.PlayerEntity;
import com.halotroop.litecraft.world.dimension.Dimension;
import com.halotroop.litecraft.world.gen.*;
import com.halotroop.litecraft.world.gen.modifier.WorldModifier;

import it.unimi.dsi.fastutil.longs.*;

public class World implements BlockAccess, WorldGenConstants
{
	Long2ObjectMap<Chunk> chunks;
	private final WorldModifier[] worldModifiers;
	private final ChunkGenerator chunkGenerator;
	private final BlockAccess genBlockAccess;
	private final LitecraftSave save;
	private final long seed;
	private final int dimension;
	private final ForkJoinPool threadPool;
	public PlayerEntity playerEntity;
	int renderBound;
	int renderBoundVertical;
	// dummy block instance for retrieving the default block model
	private final BlockInstance dummy;

	public World(long seed, int renderSize, Dimension<?> dim, LitecraftSave save)
	{
		this.threadPool = new ForkJoinPool(4, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
		this.dummy = new BlockInstance(Blocks.ANDESITE, new Vector3f(0, 0, 0));
		this.dummy.setVisible(false);
		this.chunks = new Long2ObjectArrayMap<>();
		this.seed = seed;
		this.chunkGenerator = dim.createChunkGenerator(seed);
		this.worldModifiers = dim.getWorldModifierArray();
		// initialize world modifiers with seed
		for (WorldModifier modifier : this.worldModifiers)
		{ modifier.initialize(seed); }
		this.genBlockAccess = new GenerationWorld(this);
		this.save = save;
		this.dimension = dim.id;
		this.renderBound = renderSize / 2;
		this.renderBoundVertical = this.renderBound / 2;
		if (this.renderBoundVertical < 2)
		{ this.renderBoundVertical = 2; }
	}

	public int findAir(int x, int z)
	{
		int y = SEA_LEVEL;
		int attemptsRemaining = 255;
		while (attemptsRemaining-- > 0)
		{
			// DO NOT CHANGE TO y++
			if (this.getBlock(x, ++y, z) == Blocks.AIR)
				return y;
		}
		return -1; // if it fails, returns -1
	}

	public void spawnPlayer()
	{
		int y = this.findAir(0, 0);
		if (y == -1)
			y = 300; // yeet
		this.spawnPlayer(0, y, -3);
	}

	public PlayerEntity spawnPlayer(float x, float y, float z)
	{
		this.playerEntity = (PlayerEntity) Litecraft.getInstance().player;
		this.playerEntity.setVisible(false);
		// Generate world around player
		long time = System.currentTimeMillis();
		System.out.println("Generating world!");
		this.updateLoadedChunks(this.playerEntity.getChunkX(), this.playerEntity.getChunkY(), this.playerEntity.getChunkZ());
		System.out.println("Generated world in " + (System.currentTimeMillis() - time) + " milliseconds");
		// return player
		return this.playerEntity;
	}

	public Chunk getChunk(int chunkX, int chunkY, int chunkZ)
	{
		Chunk chunk = this.chunks.computeIfAbsent(posHash(chunkX, chunkY, chunkZ), pos ->
		{
			Chunk readChunk = save.readChunk(this, chunkX, chunkY, chunkZ, this.dimension);
			return readChunk == null ? this.chunkGenerator.generateChunk(this, chunkX, chunkY, chunkZ) : readChunk;
		});
		if (chunk.isFullyGenerated()) return chunk;
		this.populateChunk(chunkX, chunkY, chunkZ, chunk.chunkStartX, chunk.chunkStartY, chunk.chunkStartZ);
		chunk.setFullyGenerated(true);
		return chunk;
	}

	public Chunk getChunkToLoad(int chunkX, int chunkY, int chunkZ)
	{
		long posHash = posHash(chunkX, chunkY, chunkZ);
		// try get an already loaded chunk
		Chunk result = this.chunks.get(posHash);
		if (result != null)
			return result;
		// try read a chunk from memory
		result = save.readChunk(this, chunkX, chunkY, chunkZ, this.dimension);
		// if neither of those work, generate the chunk
		result = result == null ? this.chunkGenerator.generateChunk(this, chunkX, chunkY, chunkZ) : result;
		// add chunk to array
		this.chunks.put(posHash, result);
		return result;
	}

	/** @return whether the chunk was unloaded without errors. Will often, but not always, be equal to whether the chunk was already in memory. */
	boolean unloadChunk(long posHash)
	{
		Chunk chunk = this.chunks.get(posHash);
		// If the chunk is not in memory, it does not need to be unloaded
		if (chunk == null) return false;
		// Otherwise save the chunk
		AtomicBoolean result = new AtomicBoolean(false);
		CompletableFuture.runAsync(() ->
		{
			result.set(this.save.saveChunk(chunk));
			this.chunks.remove(posHash);
		}, threadPool);
		return result.get();
	}

	void populateChunk(Chunk chunk)
	{ this.populateChunk(chunk.chunkX, chunk.chunkY, chunk.chunkZ, chunk.chunkStartX, chunk.chunkStartY, chunk.chunkStartZ); }

	private void populateChunk(int chunkX, int chunkY, int chunkZ, int chunkStartX, int chunkStartY, int chunkStartZ)
	{
		Random rand = new Random(this.seed + 5828671L * chunkX + -47245139L * chunkY + 8972357 * (long) chunkZ);
		for (WorldModifier modifier : this.worldModifiers)
		{ modifier.modifyWorld(this.genBlockAccess, rand, chunkStartX, chunkStartY, chunkStartZ); }
	}

	/** @return a chunk that has not neccesarily gone through chunk populating. Used in chunk populating to prevent infinite recursion. */
	Chunk getGenChunk(int chunkX, int chunkY, int chunkZ)
	{ return this.chunks.computeIfAbsent(posHash(chunkX, chunkY, chunkZ), pos -> this.chunkGenerator.generateChunk(this, chunkX, chunkY, chunkZ)); }

	long posHash(int chunkX, int chunkY, int chunkZ)
	{ return ((long) chunkX & 0x3FF) | (((long) chunkY & 0x3FF) << 10) | (((long) chunkZ & 0x3FF) << 20); }

	@Override
	public Block getBlock(int x, int y, int z)
	{ return this.getChunk(x >> POS_SHIFT, y >> POS_SHIFT, z >> POS_SHIFT).getBlock(x & MAX_POS, y & MAX_POS, z & MAX_POS); }

	@Override
	public void setBlock(int x, int y, int z, Block block)
	{ this.getChunk(x >> POS_SHIFT, y >> POS_SHIFT, z >> POS_SHIFT).setBlock(x & MAX_POS, y & MAX_POS, z & MAX_POS, block); }

	public void optimiseChunks()
	{ this.chunks.forEach((pos, chunk) -> optimiseChunk(chunk)); }

	//used for model combining and culling
	public Chunk optimiseChunk(Chunk chunk)
	{ return chunk; }

	public void render(BlockRenderer blockRenderer)
	{
		blockRenderer.prepareModel(this.dummy.getModel());
		this.chunks.forEach((pos, c) ->
		{
			if (c != null && c.isFullyGenerated())
				c.render(blockRenderer);
		});
		blockRenderer.unbindModel();
	}

	public void unloadAllChunks()
	{
		LongList chunkPositions = new LongArrayList();
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		if (this.chunks != null)
		{
			this.chunks.forEach((pos, chunk) ->
			{ // for every chunk in memory
				futures.add(CompletableFuture.runAsync(() ->
				{
					chunkPositions.add((long) pos); // add pos to chunk positions list for removal later
					this.save.saveChunk(chunk); // save chunk
				}, threadPool));
			});
		}
		futures.forEach(CompletableFuture::join);
		chunkPositions.forEach((LongConsumer) (pos -> this.chunks.remove(pos))); // remove all chunks
	}

	public long getSeed()
	{ return this.seed; }

	public static final int SEA_LEVEL = 0;

	public void updateLoadedChunks(int chunkX, int chunkY, int chunkZ)
	{
		CompletableFuture.runAsync(() ->
		{
			List<Chunk> toKeep = new ArrayList<>();
			// loop over rendered area, adding chunks that are needed
			for (int x = chunkX - this.renderBound; x < chunkX + this.renderBound; x++)
				for (int z = chunkZ - this.renderBound; z < chunkZ + this.renderBound; z++)
					for (int y = chunkY - this.renderBound; y < chunkY + this.renderBound; y++)
						toKeep.add(this.getChunkToLoad(x, y, z));
			LongList toRemove = new LongArrayList();
			// check which loaded chunks are not neccesary
			chunks.forEach((pos, chunk) ->
			{
				if (!toKeep.contains(chunk))
					toRemove.add((long) pos);
			});
			// unload unneccesary chunks from chunk array
			toRemove.forEach((LongConsumer) this::unloadChunk);
			toKeep.forEach(chunk ->
			{
				if (!chunk.isFullyGenerated())
				{
					this.populateChunk(chunk);
					chunk.setFullyGenerated(true);
				}
				boolean alreadyRendering = chunk.doRender(); // if it's already rendering then it's most likely in the map
				chunk.setRender(true);
				if (!alreadyRendering)
					chunks.put(this.posHash(chunk.chunkX, chunk.chunkY, chunk.chunkZ), chunk);
			});
		}, threadPool);
	}

	private static final class GenerationWorld implements BlockAccess, WorldGenConstants
	{
		GenerationWorld(World parent)
		{ this.parent = parent; }

		public final World parent;

		@Override
		public Block getBlock(int x, int y, int z)
		{ return this.parent.getGenChunk(x >> POS_SHIFT, y >> POS_SHIFT, z >> POS_SHIFT).getBlock(x & MAX_POS, y & MAX_POS, z & MAX_POS); }

		@Override
		public void setBlock(int x, int y, int z, Block block)
		{ this.parent.getGenChunk(x >> POS_SHIFT, y >> POS_SHIFT, z >> POS_SHIFT).setBlock(x & MAX_POS, y & MAX_POS, z & MAX_POS, block); }
	}
}
