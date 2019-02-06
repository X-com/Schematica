package mixininterfaces;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;

public interface IRenderChunkAccessor {
    public void preRenderBlocksAccess(final BufferBuilder buffer, final BlockPos pos);
}
