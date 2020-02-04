package scaveleous.mcregion;


/*
** Author: Scaveleous (Minecraft Forum)
** (Public domain)
**/
// A simple cache and wrapper for efficiently multiple RegionFiles simultaneously.
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class RegionFileCache
{
	private static final Map<File, Reference<RegionFile>> cache = new HashMap<File, Reference<RegionFile>>();

	public static synchronized void clear()
	{
		for (Reference<RegionFile> ref : cache.values())
		{
			try
			{
				if (ref.get() != null)
					ref.get().close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		cache.clear();
	}

	public static DataInputStream getChunkDataInputStream(File basePath, int x, int z)
	{
		RegionFile r = getRegionFile(basePath, x, z);
		return r.getChunkDataInputStream(x & 31, z & 31);
	}

	public static DataOutputStream getChunkDataOutputStream(File basePath, int x, int z)
	{
		RegionFile r = getRegionFile(basePath, x, z);
		return r.getChunkDataOutputStream(x & 31, z & 31);
	}

	public static synchronized RegionFile getRegionFile(File basePath, int x, int z)
	{
		File regionDir = new File(basePath, "region");
		File file = new File(regionDir, "r." + (x >> 5) + "." + (z >> 5) + ".data");
		Reference<RegionFile> ref = cache.get(file);
		if (ref != null && ref.get() != null)
			return ref.get();
		if (!regionDir.exists())
			regionDir.mkdirs();
		RegionFile reg = new RegionFile(file);
		cache.put(file, new SoftReference<RegionFile>(reg));
		return reg;
	}

	public static int getSizeDelta(File basePath, int x, int z)
	{
		RegionFile r = getRegionFile(basePath, x, z);
		return r.getSizeDelta();
	}

	private RegionFileCache()
	{}
}