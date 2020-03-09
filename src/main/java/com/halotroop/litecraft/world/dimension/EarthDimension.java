package com.halotroop.litecraft.world.dimension;

import com.halotroop.litecraft.world.gen.EarthChunkGenerator;

class EarthDimension extends Dimension<EarthChunkGenerator>
{
	public EarthDimension(int id, String saveIdentifier)
	{ super(id, saveIdentifier); }

	@Override
	public EarthChunkGenerator createChunkGenerator(long seed)
	{ return new EarthChunkGenerator(seed, this.id); }
}