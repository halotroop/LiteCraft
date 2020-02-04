package scaveleous.mcregion;

/*
** Author: Scaveleous (Minecraft Forum)
** (Public domain)
**/
// A tool to convert to and from chunk/region files
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class RegionTool
{
	static private boolean isConsole = false;

	/* copies all files from one directory to another, except for files in the skip set
	   does not copy empty directories */
	private static void copyDir(File srcDir, File dstDir, Set<File> skip)
	{
		byte buf[] = new byte[4096];
		for (File child : srcDir.listFiles())
		{
			if (child.isDirectory())
				copyDir(child, new File(dstDir, child.getName()), skip);
			else
			{
				if (!skip.contains(child))
				{
					try
					{
						File dstfile = new File(dstDir, child.getName());
						dstDir.mkdirs();
						FileOutputStream out = new FileOutputStream(dstfile);
						FileInputStream in = new FileInputStream(child);
						int len = 0;
						while (len != -1)
						{
							out.write(buf, 0, len);
							len = in.read(buf);
						}
						out.close();
						in.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static void exit(String message)
	{
		System.err.println(message);
		System.exit(1);
	}

	private static void exitUsage()
	{ exit("regionTool: converts between chunks and regions\n" +
		"usage: java -jar RegionTool.jar [un]pack <world directory> [target directory]"); }

	public static void main(String[] args)
	{
		if (args.length != 2 && args.length != 3)
			exitUsage();
		if (System.console() != null)
			isConsole = true;
		int mode = 0;
		if (args[0].equalsIgnoreCase("unpack"))
			mode = 1;
		else if (args[0].equalsIgnoreCase("pack"))
			mode = 2;
		if (mode == 0)
			exitUsage();
		File worldDir = new File(args[1]);
		if (!worldDir.exists() || !worldDir.isDirectory())
			exit("error: " + worldDir.getPath() + " is not a directory");
		File targetDir = worldDir;
		if (args.length == 3)
		{
			targetDir = new File(args[2]);
			if (!targetDir.isDirectory())
			{ targetDir.mkdirs(); }
		}
		if (mode == 1)
			unpack(worldDir, targetDir);
		else if (mode == 2)
			pack(worldDir, targetDir);
	}

	private static void pack(File worldDir, File targetDir)
	{
		Set<File> processedFiles = null;
		if (worldDir != targetDir)
			processedFiles = new HashSet<File>();
		Pattern chunkFilePattern = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+).dat");
		Pattern chunkFolderPattern = Pattern.compile("[0-9a-z]|1[0-9a-r]");
		int chunksPacked = 0;
		int chunksSkipped = 0;
		for (File dir1 : worldDir.listFiles())
		{
			if (!dir1.isDirectory())
				continue;
			if (chunkFolderPattern.matcher(dir1.getName()).matches())
			{
				for (File dir2 : dir1.listFiles())
				{
					if (!dir2.isDirectory())
						continue;
					if (chunkFolderPattern.matcher(dir2.getName()).matches())
					{
						for (File chunkFile : dir2.listFiles())
						{
							Matcher m = chunkFilePattern.matcher(chunkFile.getName());
							if (m.matches())
							{
								if (packChunk(targetDir, chunkFile, m))
									chunksPacked++;
								else
									chunksSkipped++;
								if (processedFiles != null)
									processedFiles.add(chunkFile);
							}
							if (isConsole)
								System.out.print("\rpacked " + chunksPacked + " chunks" +
									(chunksSkipped > 0 ? ", skipped " + chunksSkipped + " older ones" : ""));
						}
					}
				}
			}
		}
		if (isConsole)
			System.out.print("\r");
		System.out.println("packed " + chunksPacked + " chunks" +
			(chunksSkipped > 0 ? ", skipped " + chunksSkipped + " older ones" : ""));
		if (processedFiles != null)
			copyDir(worldDir, targetDir, processedFiles);
	}

	private static boolean packChunk(File worldDir, File chunkFile, Matcher m)
	{
		int x = Integer.parseInt(m.group(1), 36);
		int z = Integer.parseInt(m.group(2), 36);
		RegionFile region = RegionFileCache.getRegionFile(worldDir, x, z);
		if (region.lastModified() > chunkFile.lastModified())
			return false;
		byte buf[] = new byte[4096];
		int len = 0;
		try
		{
			DataInputStream istream = new DataInputStream(
				new GZIPInputStream(new FileInputStream(chunkFile)));
			DataOutputStream out = region.getChunkDataOutputStream(x & 31, z & 31);
			while (len != -1)
			{
				out.write(buf, 0, len);
				len = istream.read(buf);
			}
			out.close();
			istream.close();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	private static void unpack(File worldDir, File targetDir)
	{
		File regionDir = new File(worldDir, "region");
		if (!regionDir.exists())
			exit("error: region directory not found");
		Set<File> processedFiles = null;
		if (worldDir != targetDir)
			processedFiles = new HashSet<File>();
		Pattern regionFilePattern = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+).data");
		Matcher match;
		for (File file : regionDir.listFiles())
		{
			if (!file.isFile())
				continue;
			match = regionFilePattern.matcher(file.getName());
			if (match.matches())
			{
				unpackRegionFile(targetDir, file, match);
				if (processedFiles != null)
					processedFiles.add(file);
			}
		}
		if (processedFiles != null)
			copyDir(worldDir, targetDir, processedFiles);
	}

	private static void unpackRegionFile(File worldDir, File file, Matcher match)
	{
		long regionModified = file.lastModified();
		RegionFile region = new RegionFile(file);
		String name = file.getName();
		int regionX = Integer.parseInt(match.group(1));
		int regionZ = Integer.parseInt(match.group(2));
		int nWritten = 0, nSkipped = 0;
		for (int x = 0; x < 32; ++x)
		{
			for (int z = 0; z < 32; ++z)
			{
				DataInputStream istream = region.getChunkDataInputStream(x, z);
				if (istream == null)
					continue;
				int chunkX = x + (regionX << 5);
				int chunkZ = z + (regionZ << 5);
				String chunkName = "c." + Integer.toString(chunkX, 36) + "." + Integer.toString(chunkZ, 36) + ".dat";
				File chunkFile = new File(worldDir, Integer.toString(chunkX & 63, 36));
				chunkFile = new File(chunkFile, Integer.toString(chunkZ & 63, 36));
				if (!chunkFile.exists())
					chunkFile.mkdirs();
				chunkFile = new File(chunkFile, chunkName);
				byte buf[] = new byte[4096];
				int len = 0;
				if (chunkFile.lastModified() > regionModified)
				{
					nSkipped++;
				}
				else
				{
					try
					{
						DataOutputStream out = new DataOutputStream(
							new GZIPOutputStream(new FileOutputStream(chunkFile)));
						while (len != -1)
						{
							out.write(buf, 0, len);
							len = istream.read(buf);
						}
						out.close();
						nWritten++;
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				if (isConsole)
					System.out.print("\r" + name + ": unpacked " + nWritten + " chunks" +
						(nSkipped > 0 ? ", skipped " + nSkipped + " newer ones" : ""));
			}
		}
		if (isConsole)
			System.out.print("\r");
		System.out.println(name + ": unpacked " + nWritten + " chunks" +
			(nSkipped > 0 ? ", skipped " + nSkipped + " newer ones" : ""));
	}
}