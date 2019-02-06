package com.github.lunatrius.schematica.client.renderer.chunk.proxy;

import com.github.lunatrius.schematica.client.renderer.SchematicRenderCache;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import mcp.MethodsReturnNonnullByDefault;
import mixin.IMixinRenderChunk;
import mixininterfaces.IRenderChunk;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SchematicRenderChunkVbo extends RenderChunk implements IRenderChunk {
    public SchematicRenderChunkVbo(final World world, final RenderGlobal renderGlobal, final int index) {
        super(world, renderGlobal, index);
    }

    @Override
    public void rebuildChunk(final float x, final float y, final float z, final ChunkCompileTaskGenerator generator) {
        generator.getLock().lock();

        try {
            if (generator.getStatus() == ChunkCompileTaskGenerator.Status.COMPILING) {
                final BlockPos from = getPosition();
                final SchematicWorld schematic = (SchematicWorld) ((IMixinRenderChunk)this).getWorld();

                if (from.getX() < 0 || from.getZ() < 0 || from.getX() >= schematic.getWidth() || from.getZ() >= schematic.getLength()) {
                    final SetVisibility visibility = new SetVisibility();
                    visibility.setAllVisible(true);

                    final CompiledChunk dummy = new CompiledChunk();
                    dummy.setVisibility(visibility);

                    generator.setCompiledChunk(dummy);
                    return;
                }
            }
        } finally {
            generator.getLock().unlock();
        }

        super.rebuildChunk(x, y, z, generator);
    }

//    public void createRegionRenderCache(final World world, final BlockPos from, final BlockPos to, final int subtract) {
//        ((IMixinRenderChunk) this).setWorldView(new SchematicRenderCache(world, from, to, subtract));
//    }

    public boolean renderCash(){
        return true;
    }

    public ChunkCache getRenderCash(final World world, final BlockPos from, final BlockPos to, final int subtract){
        return new SchematicRenderCache(world, from, to, subtract);
    }
}
