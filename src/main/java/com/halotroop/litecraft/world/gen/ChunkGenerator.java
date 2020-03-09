package com.halotroop.litecraft.world.gen;

import com.halotroop.litecraft.world.*;

public interface ChunkGenerator
{
	Chunk generateChunk(World world, int chunkX, int chunkY, int chunkZ);
}
