package com.github.lunatrius.schematica.util;

import net.minecraft.nbt.NBTBase;

import java.io.DataOutput;

public interface NBTAccessor {
	static void callWriteEntry(String name, NBTBase data, DataOutput output) {}
}
