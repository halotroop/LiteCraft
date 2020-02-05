package com.github.halotroop.litecraft.input;

/*
 * Author: Valoeghese
 */
public final class InitialPressHandler implements KeyListener
{
	public InitialPressHandler(KeyCallback callback)
	{ this.callback = callback; }

	private boolean activatedPreviously = false;
	private final KeyCallback callback;

	@Override
	public void listen(boolean active)
	{
		if (!activatedPreviously && active)
		{ callback.onCallback(); }
		activatedPreviously = active;
	}
}
