package mixin.optifine;

import mixininterfaces.IRenderChunk;
import mixininterfaces.IRenderChunkAccessor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderChunk.class)
public class MixinRenderChunkOptifine implements IRenderChunk {

    @Redirect(method = "createRegionRenderCache", at = @At(value = "NEW", target = "net/minecraft/world/ChunkCache"), remap = false, require = 0)
    private ChunkCache whatever(World world, BlockPos from, BlockPos to, int subtract) {
        if(renderCash()){
            return getRenderCash(world, from, to, subtract);
        }
        return new ChunkCache(world, from, to, subtract);
    }

    public boolean renderCash() {
        return false;
    }

    @Override
    public ChunkCache getRenderCash(World world, BlockPos from, BlockPos to, int subtract) {
        return null;
    }
}
