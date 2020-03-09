package com.halotroop.litecraft.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.*;

import com.github.hydos.ginger.engine.opengl.utils.GLLoader;
import com.halotroop.litecraft.types.block.*;

public class VoxelLoader extends GLLoader
{
	public static int createBlockAtlas()
	{
		int width = 16;
		int height = 16;
		//Prepare the atlas texture and gen it
		int atlasId = GL11.glGenTextures();
		//Bind it to openGL
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlasId);
		//Apply the settings for the texture
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		//Fill the image with blank image data
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width * 2, height * 2, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		long maxX = Math.round(Math.sqrt(Blocks.blocks.size()));
		int currentX = 0;
		int currentY = 0;
		for (Block block : Blocks.blocks)
		{
			//just in case
			if (!block.texture.equals("DONTLOAD"))
			{
				System.out.println(block.texture);
				block.updateBlockModelData();
				if (currentX > maxX)
				{
					currentX = 0;
					currentY--;
				}
				GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0,
					currentX * width, currentY * height,
					width, height,
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
					block.model.getTexture().getTexture().getImage());
				currentX++;
			}
		}
		return atlasId;
	}
}
