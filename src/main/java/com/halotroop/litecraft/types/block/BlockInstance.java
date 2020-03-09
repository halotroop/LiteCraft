package com.halotroop.litecraft.types.block;

import org.joml.Vector3f;

import com.github.hydos.ginger.engine.common.elements.objects.RenderObject;
import com.halotroop.litecraft.world.Chunk;

public class BlockInstance extends RenderObject
{
	public BlockInstance(Block block, Vector3f position)
	{ super(block.model, position, 0, 0, 0, new Vector3f(1f, 1f, 1f)); }

	public void processCulling(Chunk chunk)
	{
		Vector3f southNeighbourBlockLocation = this.getPosition();
		southNeighbourBlockLocation.x--;
	}
}
