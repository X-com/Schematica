package mixin;

import mixininterfaces.IRenderChunkAccessor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {
    private final boolean isRenderChunk = this instanceof IRenderChunkAccessor;

    @Inject(method = "preRenderBlocks", at = @At("HEAD"), cancellable = true)
    private void cheatyPreRenderBlocks(BufferBuilder buffer, BlockPos pos, CallbackInfo ci) {
        if (isRenderChunk) {
            ((IRenderChunkAccessor) this).preRenderBlocks(buffer, pos);
            ci.cancel();
        }
    }
}
