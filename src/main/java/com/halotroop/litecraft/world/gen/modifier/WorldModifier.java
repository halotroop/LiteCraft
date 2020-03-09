package com.halotroop.litecraft.world.gen.modifier;

import java.util.Random;

import com.halotroop.litecraft.world.BlockAccess;

public interface WorldModifier
{
	void modifyWorld(BlockAccess world, Random rand, int chunkStartX, int chunkStartY, int chunkStartZ);

	void initialize(long seed);
}
