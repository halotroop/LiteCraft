package com.halotroop.litecraft.types.entity;

import org.joml.Vector3f;

import com.github.hydos.ginger.engine.common.Constants;
import com.github.hydos.ginger.engine.common.api.GingerRegister;
import com.github.hydos.ginger.engine.common.io.Window;
import com.github.hydos.ginger.engine.opengl.render.models.GLTexturedModel;
import com.halotroop.litecraft.Litecraft;
import com.halotroop.litecraft.util.RelativeDirection;
import com.halotroop.litecraft.world.gen.WorldGenConstants;

public class PlayerEntity extends Entity implements WorldGenConstants
{
	private boolean isInAir = false;
	private double upwardsSpeed;
	private boolean noWeight = true;
	private int chunkX, chunkY, chunkZ;

	public PlayerEntity(GLTexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, Vector3f scale)
	{
		super(model, position, rotX, rotY, rotZ, scale);
		this.chunkX = (int) position.x >> POS_SHIFT;
		this.chunkY = (int) position.y >> POS_SHIFT;
		this.chunkZ = (int) position.z >> POS_SHIFT;
	}

	public void move(RelativeDirection direction)
	{
		float ry = (float) Math.toRadians(GingerRegister.getInstance().game.data.camera.getYaw());
		switch (direction)
		{
		default:
		case FORWARD:
			position.z -= Math.cos(ry) * Constants.movementSpeed;
			position.x += Math.sin(ry) * Constants.movementSpeed;
			break;
		case BACKWARD:
			position.z += Math.cos(ry) * Constants.movementSpeed;
			position.x -= Math.sin(ry) * Constants.movementSpeed;
			break;
		case LEFT:
			ry -= RIGHT_ANGLE;
			position.z -= Math.cos(ry) * Constants.movementSpeed;
			position.x += Math.sin(ry) * Constants.movementSpeed;
			break;
		case RIGHT:
			ry += RIGHT_ANGLE;
			position.z -= Math.cos(ry) * Constants.movementSpeed;
			position.x += Math.sin(ry) * Constants.movementSpeed;
			break;
		case UP:
			if (this.noWeight)
				position.y += Constants.movementSpeed;
			else this.jump();
			break;
		case DOWN:
			position.y -= Constants.movementSpeed;
			break;
		}
	}

	private static final float RIGHT_ANGLE = (float) (Math.PI / 2f);

	private void jump()
	{
		if (!isInAir)
		{
			isInAir = true;
			this.upwardsSpeed = Constants.jumpPower;
		}
	}

	public int getChunkX()
	{ return this.chunkX; }

	public int getChunkY()
	{ return this.chunkY; }

	public int getChunkZ()
	{ return this.chunkZ; }

	public void updateMovement()
	{
		super.increasePosition(0, (float) (upwardsSpeed * (Window.getTime())), 0);
		upwardsSpeed += Constants.gravity.y() * Window.getTime(); // TODO: Implement 3D gravity
		isInAir = false;
		upwardsSpeed = 0;
		int newChunkX = (int) position.x >> POS_SHIFT;
		int newChunkY = (int) position.y >> POS_SHIFT;
		int newChunkZ = (int) position.z >> POS_SHIFT;
		if (newChunkX != this.chunkX || newChunkY != this.chunkY || newChunkZ != this.chunkZ)
		{
			Litecraft.getInstance().getWorld().updateLoadedChunks(newChunkX, newChunkY, newChunkZ);
			this.chunkX = newChunkX;
			this.chunkY = newChunkY;
			this.chunkZ = newChunkZ;
		}
	}
}
