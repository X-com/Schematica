package mixin;

import com.github.lunatrius.schematica.ShittyInvokerInterface;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.DataOutput;
import java.io.IOException;

@Mixin(NBTTagCompound.class)
public abstract class MixinNBTTagCompound implements ShittyInvokerInterface {
	@Shadow
	private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {}

	private static void callWriteEntry(String name, NBTBase data, DataOutput output) throws IOException {
		writeEntry(name, data, output);
	}
}
