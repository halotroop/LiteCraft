package com.halotroop.litecraft.types.block;

import java.util.ArrayList;

import com.halotroop.litecraft.types.block.Block.Properties;

public final class Blocks
{
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	public static final Block AIR = new Block(new Properties("air").visible(false).fullCube(false));
	public static final Block GRASS = new Block(new Properties("cubes/soil/grass/grass_top.png").caveCarveThreshold(0.04f));
	public static final Block DIRT = new Block("cubes/soil/dirt.png", new Properties("dirt").caveCarveThreshold(0.04f));
	public static final Block ANDESITE = new Block("cubes/stone/basic/andesite.png", new Properties("andesite").caveCarveThreshold(0.08f));
	public static final Block DIORITE = new Block("cubes/stone/basic/diorite.png", new Properties("diorite").caveCarveThreshold(0.05f));
	public static final Block GRANITE = new Block("cubes/stone/basic/granite.png", new Properties("granite").caveCarveThreshold(0.06f));
	public static final Block GNEISS = new Block("cubes/stone/basic/gneiss.png", new Properties("gneiss").caveCarveThreshold(0.09f));

	public static Block init()
	{ return AIR; }
}
