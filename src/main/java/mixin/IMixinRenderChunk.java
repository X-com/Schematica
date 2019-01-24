package mixin;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderChunk.class)
public interface IMixinRenderChunk {
    @Accessor
    World getWorld();
}
