package com.github.lunatrius.schematica;

import net.minecraft.nbt.NBTBase;

import java.io.DataOutput;

public interface ShittyInvokerInterface {
	static void callWriteEntry(String name, NBTBase data, DataOutput output) {}
}
