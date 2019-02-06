package mixininterfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public interface IRenderChunk {
    public boolean renderCash();
    public ChunkCache getRenderCash(final World world, final BlockPos from, final BlockPos to, final int subtract);
}
