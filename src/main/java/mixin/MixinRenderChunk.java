package mixin;

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
public class MixinRenderChunk implements IRenderChunk {

    @Shadow
    private World world;
    @Shadow
    @Final
    BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos(-1, -1, -1);

    private final boolean isRenderChunk = this instanceof IRenderChunkAccessor;

    @Inject(method = "preRenderBlocks", at = @At("HEAD"), cancellable = true)
    private void cheatyPreRenderBlocks(BufferBuilder buffer, BlockPos pos, CallbackInfo ci) {
        if (isRenderChunk) {
            ((IRenderChunkAccessor) this).preRenderBlocksAccess(buffer, pos);
            ci.cancel();
        }
    }
//
//    @Inject(method = "rebuildWorldView", at = @At("RETURN"))
//    private void renderCash(CallbackInfo ci) {
//        createRegionRenderCache(world, position.add(-1, -1, -1), position.add(16, 16, 16), 1);
//    }

    @Redirect(method = "rebuildWorldView", at = @At(value = "NEW", target = "net/minecraft/world/ChunkCache"), require = 0)
    private ChunkCache whatever(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
        if(renderCash()){
            return getRenderCash(worldIn, posFromIn, posToIn, subIn);
        }
        return new ChunkCache(this.world, this.position.add(-1, -1, -1), this.position.add(16, 16, 16), 1);
    }

    public boolean renderCash() {
        return false;
    }

    @Override
    public ChunkCache getRenderCash(World world, BlockPos from, BlockPos to, int subtract) {
        return null;
    }
}
