package com.github.halotroop.litecraft;

import java.io.*;

public class TextFileReader
{
	public static String[] readFileToStringArray(String filename) throws IOException
	{
		ClassLoader.getSystemClassLoader();
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename);
		InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
		BufferedReader in = new BufferedReader(streamReader);
		String[] output = new String[] {};
		for (String line; (line = in.readLine()) != null;)
		{
			String[] bufferArray = new String[output.length + 1];
			System.arraycopy(output, 0, bufferArray, 0, output.length);
			bufferArray[output.length] = line;
			output = bufferArray;
		}
		return output;
	}
}
