package mixin;

import com.github.lunatrius.schematica.util.ITileEntity;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntity.class)
public class MixinTileEntity implements ITileEntity {

    @Shadow
    protected World world;

    @Shadow
    public Block getBlockType() {
        return null;
    }

    @Shadow
    public BlockPos getPos() {
        return null;
    }

    private static final net.minecraft.util.math.AxisAlignedBB INFINITE_EXTENT_AABB = new net.minecraft.util.math.AxisAlignedBB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    /**
     * Forge Method
     */
    public AxisAlignedBB getRenderBoundingBox() {
        net.minecraft.util.math.AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        Block type = getBlockType();
        BlockPos pos = getPos();
        if (type == Blocks.ENCHANTING_TABLE) {
            bb = new net.minecraft.util.math.AxisAlignedBB(pos, pos.add(1, 1, 1));
        } else if (type == Blocks.CHEST || type == Blocks.TRAPPED_CHEST) {
            bb = new net.minecraft.util.math.AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
        } else if (type == Blocks.STRUCTURE_BLOCK) {
            bb = INFINITE_EXTENT_AABB;
        } else if (type != null && type != Blocks.BEACON) {
            net.minecraft.util.math.AxisAlignedBB cbb = null;
            try {
                cbb = world.getBlockState(getPos()).getCollisionBoundingBox(world, pos).offset(pos);
            } catch (Exception e) {
                cbb = new net.minecraft.util.math.AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(1, 1, 1));
            }
            if (cbb != null) bb = cbb;
        }
        return bb;
    }
}
