package com.halotroop.litecraft.world.dimension;

import java.util.*;

import com.halotroop.litecraft.world.gen.ChunkGenerator;
import com.halotroop.litecraft.world.gen.modifier.WorldModifier;

import it.unimi.dsi.fastutil.ints.*;

public abstract class Dimension<T extends ChunkGenerator>
{
	public List<WorldModifier> worldModifiers = new ArrayList<>();
	public final int id;
	public final String saveIdentifier;

	public Dimension(int id, String saveIdentifier)
	{
		this.id = id;
		this.saveIdentifier = saveIdentifier;
		ID_TO_DIMENSION.put(id, this);
	}

	public Dimension<T> addWorldModifier(WorldModifier modifier)
	{
		this.worldModifiers.add(modifier);
		return this;
	}

	public WorldModifier[] getWorldModifierArray()
	{ return this.worldModifiers.toArray(new WorldModifier[0]); }

	public abstract T createChunkGenerator(long seed);

	public static Dimension<?> getById(int id)
	{ return ID_TO_DIMENSION.get(id); }

	private static final Int2ObjectMap<Dimension<?>> ID_TO_DIMENSION = new Int2ObjectArrayMap<>();
}
