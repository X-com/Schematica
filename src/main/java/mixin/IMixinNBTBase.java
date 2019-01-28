package mixin;

import net.minecraft.nbt.NBTBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.DataOutput;
import java.io.IOException;

@Mixin(NBTBase.class)
public interface IMixinNBTBase {
    @Invoker
    public void callWrite(DataOutput output) throws IOException;
}
