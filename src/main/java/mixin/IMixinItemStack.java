package mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStack.class)
public interface IMixinItemStack {

    @Accessor
    public NBTTagCompound getStackTagCompound();

    @Accessor
    public void setStackTagCompound(NBTTagCompound var);
}
