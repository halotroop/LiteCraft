package com.github.halotroop.litecraft.options;

import org.aeonbits.owner.Config;

@Config.Sources("file:~/Documents/LiteCraft.config")
public interface SettingsConfig extends Config
{
	@Key("screen_width")
	@DefaultValue("640")
	public int screenWidth();
	@Key("screen_height")
	@DefaultValue("480")
	public int screenHeight();
	@Key("debug_mode")
	@DefaultValue("false")
	public boolean debugMode();
	@Key("spam_log")
	@DefaultValue("false")
	public boolean spamLog();
}
