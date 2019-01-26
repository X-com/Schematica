package mixininterfaces;

import net.minecraft.nbt.NBTBase;

import java.io.DataOutput;

public interface INBTAccessor {
	static void callWriteEntry(String name, NBTBase data, DataOutput output) {}
}
