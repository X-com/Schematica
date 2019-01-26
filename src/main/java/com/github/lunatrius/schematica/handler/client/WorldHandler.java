package com.github.lunatrius.schematica.handler.client;

import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class WorldHandler {
    public static WorldHandler INSTANCE = new WorldHandler();

    public void onLoad(World world) {
        if (world != null && world.isRemote && !(world instanceof SchematicWorld)) {
//            RenderSchematic.INSTANCE.setWorldAndLoadRenderers(ClientProxy.schematic);
//            addWorldAccess(world, RenderSchematic.INSTANCE);
        }
    }

    public void onUnload(World world) {
        if (world != null && world.isRemote) {
//            removeWorldAccess(world, RenderSchematic.INSTANCE);
        }
    }

    public static void addWorldAccess(final World world, final IWorldEventListener schematic) {
        if (world != null && schematic != null) {
            Reference.logger.debug("Adding world access to {}", world);
            world.addEventListener(schematic);
        }
    }

    public static void removeWorldAccess(final World world, final IWorldEventListener schematic) {
        if (world != null && schematic != null) {
            Reference.logger.debug("Removing world access from {}", world);
            world.removeEventListener(schematic);
        }
    }
}
