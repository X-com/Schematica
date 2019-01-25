package mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface IMixinMinecraft {

    @Accessor
    TextureManager getRenderEngine();

    @Invoker
    ItemStack callStoreTEInStack(ItemStack stack, TileEntity te);
}
