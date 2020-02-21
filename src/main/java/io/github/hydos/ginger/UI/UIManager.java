package io.github.hydos.ginger.UI;

import io.github.hydos.ginger.UI.canvases.WelcomeScreen;
import io.github.hydos.ginger.UI.enums.UIColourType;
import io.github.hydos.ginger.engine.elements.GuiTexture;

public class UIManager
{
	UIColourType colourMode = UIColourType.dark;
	GuiTexture background;
	UICanvas welcomeScreen;

	public UIManager(UIColourType type)
	{
		if (type == UIColourType.dark)
		{
			//			background = new GuiTexture(Loader.loadTextureDirectly("/engine/ui/dark/background/background.png"), new Vector2f(0,0), new Vector2f(10,10));
		}
		this.colourMode = type;
		welcomeScreen = new WelcomeScreen();
	}

	public void update()
	{ welcomeScreen.update(); }

	public GuiTexture getBackgroundTexture()
	{ return background; }
}
