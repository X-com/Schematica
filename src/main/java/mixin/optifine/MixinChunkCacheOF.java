package mixin.optifine;

import mixin.IMixinRenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo @Mixin(targets = "ChunkCacheOF")
public class MixinChunkCacheOF {

    @Shadow public @Final
    ChunkCache chunkCache;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void replaceChunkCache(ChunkCache chunkCache, BlockPos posFromIn, BlockPos posToIn, int subIn, CallbackInfo callbackInfo) {
//        BlockPos.MutableBlockPos position = ((IMixinRenderChunk)this).getPosition();
//        chunkCache = new ChunkCache(((IMixinRenderChunk)this).getWorld(), position.add(-1, -1, -1), position.add(16, 16, 16), 1);
    }
}
