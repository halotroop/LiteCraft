package com.halotroop.litecraft.save;

import java.io.*;
import java.util.Random;

import org.joml.Vector3f;

import com.github.hydos.ginger.engine.common.cameras.Camera;
import com.halotroop.litecraft.Litecraft;
import com.halotroop.litecraft.types.entity.PlayerEntity;
import com.halotroop.litecraft.world.*;
import com.halotroop.litecraft.world.dimension.Dimension;

import tk.valoeghese.sod.*;

public final class LitecraftSave
{
	public LitecraftSave(String name, boolean mustCreateNew)
	{
		StringBuilder sb = new StringBuilder(SAVE_DIR).append(name);
		File saveDir = new File(sb.toString());
		if (mustCreateNew)
		{
			while (saveDir.exists())
			{
				sb.append('_'); // append "_" to the save name until we get a unique save, if we must create a new save
				saveDir = new File(sb.toString());
			}
		}
		this.file = saveDir;
		this.file.mkdirs();
	}

	private final File file;

	public boolean saveChunk(Chunk chunk)
	{
		StringBuilder fileLocBuilder = new StringBuilder(this.file.getPath())
			.append('/').append(Dimension.getById(chunk.dimension).saveIdentifier)
			.append('/').append(chunk.chunkX)
			.append('/').append(chunk.chunkZ);
		File chunkDir = new File(fileLocBuilder.toString());
		chunkDir.mkdirs(); // create directory for file if it does not exist
		// format: <save dir>/<dim>/<chunkX>/<chunkZ>/<chunkY>.sod
		File chunkFile = new File(fileLocBuilder.append('/').append(chunk.chunkY).append(".sod").toString());
		try
		{
			chunkFile.createNewFile();
			BinaryData data = new BinaryData(); // create new empty binary data
			chunk.write(data); // write the chunk info to the binary data
			return data.write(chunkFile); // write the data to the file, return whether an io exception occurred
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false; // io exception = chunk writing failed at some point
		}
	}

	public Chunk readChunk(World world, int chunkX, int chunkY, int chunkZ, int dimension)
	{
		// format: <save dir>/<dim>/<chunkX>/<chunkZ>/<chunkY>.sod
		File chunkFile = new File(new StringBuilder(this.file.getPath())
			.append('/').append(Dimension.getById(dimension).saveIdentifier)
			.append('/').append(chunkX)
			.append('/').append(chunkZ)
			.append('/').append(chunkY).append(".sod").toString());
		if (chunkFile.isFile())
		{
			BinaryData data = BinaryData.read(chunkFile);
			Chunk result = new Chunk(world, chunkX, chunkY, chunkZ, dimension); // create chunk
			result.read(data); // load the chunk data we have just read into the chunk
			return result;
		}
		else return null;
	}

	public World getWorldOrCreate(Dimension<?> dim)
	{
		File globalDataFile = new File(this.file.getPath() + "/global_data.sod");
		if (globalDataFile.isFile()) // load world
		{
			BinaryData data = BinaryData.read(globalDataFile); // read data from the world file
			DataSection properties = data.get("properties"); // get the properties data section from the data that we have just read
			DataSection playerData = data.get("player");
			long seed = 0; // default seed if we cannot read it is 0
			float playerX = 0, playerY = 0, playerZ = -3; // default player x/y/z
			float playerYaw = 0.0f;
			try // try read the seed from the file
			{
				seed = properties.readLong(0); // seed is at index 0
				playerX = playerData.readFloat(0); // player x/y/z is at index 0/1/2 respectively
				playerY = playerData.readFloat(1);
				playerZ = playerData.readFloat(2);
				Camera camera = Litecraft.getInstance().getCamera(); // get camera
				camera.setPitch(playerData.readFloat(3)); // read pitch, yaw, roll from 3/4/5
				playerYaw = playerData.readFloat(4);
				camera.setYaw(playerYaw);
				camera.setRoll(playerData.readFloat(5));
			}
			catch (Throwable e)
			{
				System.out.println("Exception in reading save data! This may be benign, merely an artifact of an update to the contents of the world save data.");
			}
			World world = new World(seed, RENDER_SIZE, dim, this); // create new world with seed read from file or 0, if it could not be read
			world.spawnPlayer(playerX, playerY, playerZ); // spawn player in world
			return world;
		}
		else // create world
		{
			long seed = new Random().nextLong();
			try
			{
				globalDataFile.createNewFile(); // create world file
				this.writeGlobalData(globalDataFile, seed, new Vector3f(0, 0, -3)); // write global data with default player pos
			}
			catch (IOException e)
			{
				// If this fails the world seed will not be consistent across saves
				e.printStackTrace();
			}
			World world = new World(seed, RENDER_SIZE, dim, this); // create new world with generated seed
			world.spawnPlayer(); // spawn player in world
			return world;
		}
	}

	public void saveGlobalData(long seed, PlayerEntity playerEntity)
	{
		try
		{
			File globalDataFile = new File(this.file.getPath() + "/global_data.sod");
			globalDataFile.createNewFile(); // create world file if it doesn't exist.
			writeGlobalData(globalDataFile, seed, playerEntity.getPosition());
		}
		catch (IOException e)
		{
			System.err.println("A critical error occurred while trying to save world data!");
			e.printStackTrace();
		}
	}

	private void writeGlobalData(File globalDataFile, long seed, Vector3f playerPos)
	{
		BinaryData data = new BinaryData(); // create empty binary data
		DataSection properties = new DataSection(); // create empty data section for properties
		properties.writeLong(seed); // write seed at index 0
		DataSection playerData = new DataSection();
		playerData.writeFloat(playerPos.x); // default spawn player x/y/z
		playerData.writeFloat(playerPos.y);
		playerData.writeFloat(playerPos.z);
		Camera camera = Litecraft.getInstance().getCamera();
		playerData.writeFloat(camera.getPitch());
		playerData.writeFloat(camera.getYaw());
		playerData.writeFloat(camera.getRoll());
		data.put("properties", properties); // add properties section to data
		data.put("player", playerData); // add player section to data
		data.write(globalDataFile); // write to file
	}

	private static final String SAVE_DIR = "./saves/";
	private static final int RENDER_SIZE = 5;
}
