package com.github.halotroop.litecraft.options;

import org.aeonbits.owner.Config;

@Config.Sources("file:~/Documents/LiteCraft.config")
public interface SettingsConfig extends Config
{
	@Key("render.screen_width")
	@DefaultValue("640")
	public int screenWidth();

	@Key("render.screen_height")
	@DefaultValue("480")
	public int screenHeight();

	@Key("render.max_fps")
	@DefaultValue("60")
	public int max_fps();

	@Key("debug.debug_mode")
	@DefaultValue("false")
	public boolean debugMode();

	@Key("debug.spam_log")
	@DefaultValue("false")
	public boolean spamLog();
}
