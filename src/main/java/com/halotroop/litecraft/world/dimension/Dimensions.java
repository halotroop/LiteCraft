package com.halotroop.litecraft.world.dimension;

import com.halotroop.litecraft.world.gen.EarthChunkGenerator;
import com.halotroop.litecraft.world.gen.modifier.CavesModifier;

public final class Dimensions
{
	public static final Dimension<EarthChunkGenerator> OVERWORLD = new EarthDimension(0, "earth").addWorldModifier(new CavesModifier());
}
