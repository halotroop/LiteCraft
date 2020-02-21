package com.github.halotroop.litecraft.options;

import org.apache.commons.cli.*;

public class SettingsHandler
{
	public static Options createCommandLineOptions()
	{
		Options cmdOptions = new Options();
		cmdOptions.addOption(new Option("w", "width", true, "Screen width"));
		cmdOptions.addOption(new Option("h", "height", true, "Screen height"));
		cmdOptions.addOption(new Option("debug", "debug", true, "Use debug features"));
		cmdOptions.addOption(new Option("spam_log", "spam_log", true, "Log sanity checks"));
		cmdOptions.addOption(new Option("limit_fps", "limit_fps", true, "Use the FPS limiter"));
		cmdOptions.addOption(new Option("max_fps", "max_fps", true, "The maximum amount of FPS"));
		return cmdOptions;
	}
}
