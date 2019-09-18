package com.github.halotroop.litecraft.types.gui;

import com.bwyap.engine.gui.GUI;
import com.bwyap.engine.gui.element.TexturedButton;
import com.bwyap.engine.gui.element.base.Button;

public class MainMenu extends Menu
{
	public MainMenu(float width, float height)
	{
		super(width, height);
		Button b = new TexturedButton(20, 20, 100, 100)
		{
			@Override
			public void onMouseClicked(float x, float y, int mouseButton)
			{
				// TODO Auto-generated method stub
			}
		};
	}
}
