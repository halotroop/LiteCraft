package com.halotroop.litecraft.world.gen;

public interface WorldGenConstants
{
	int POS_SHIFT = 4;
	int DOUBLE_SHIFT = POS_SHIFT * 2;
	int CHUNK_SIZE = (int) Math.pow(2, POS_SHIFT);
	int MAX_POS = CHUNK_SIZE - 1;
}
