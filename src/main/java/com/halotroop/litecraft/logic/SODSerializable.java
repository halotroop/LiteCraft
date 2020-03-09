package com.halotroop.litecraft.logic;

import tk.valoeghese.sod.BinaryData;

public interface SODSerializable
{
	void read(BinaryData data);

	void write(BinaryData data);
}
