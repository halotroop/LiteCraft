package io.github.hydos.ginger.UI;

import java.util.List;

import io.github.hydos.ginger.UI.enums.UIDefaultClipSide;
import io.github.hydos.ginger.UI.enums.UIType;
import io.github.hydos.ginger.engine.elements.GuiTexture;

public abstract class UICanvas {
	
	UIDefaultClipSide clippingSide = UIDefaultClipSide.centre;
	UIType type = UIType.tab;
	String tabName = "Welcome";
	
	
	
	public UICanvas() {
		
	}
	
	public abstract void update();
	
	public abstract void hide(List<GuiTexture> textures);
	
	public abstract void show(List<GuiTexture> textures);
	
}
