package mixin;

import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.DataOutput;

@Mixin(NBTTagCompound.class)
public interface IMixinNBTTagCompound {
	@Invoker
	void callWrite(DataOutput output);
}
