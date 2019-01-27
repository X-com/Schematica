package com.github.lunatrius.schematica.handler.client;

import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RenderTickHandler {
    public static final RenderTickHandler INSTANCE = new RenderTickHandler();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private RenderTickHandler() {
    }

    public void onRenderTick() {
        final SchematicWorld schematic = ClientProxy.schematic;

        try {
            ClientProxy.objectMouseOver = schematic != null ? rayTrace(schematic, 1.0f) : null;
        }catch(Exception e){}
    }

    private RayTraceResult rayTrace(final SchematicWorld schematic, final float partialTicks) {
        final Entity renderViewEntity = this.minecraft.getRenderViewEntity();
        if (renderViewEntity == null) {
            return null;
        }

        final double blockReachDistance = this.minecraft.playerController.getBlockReachDistance();

        final double posX = renderViewEntity.posX;
        final double posY = renderViewEntity.posY;
        final double posZ = renderViewEntity.posZ;

        renderViewEntity.posX -= schematic.position.x;
        renderViewEntity.posY -= schematic.position.y;
        renderViewEntity.posZ -= schematic.position.z;

        final Vec3d vecPosition = renderViewEntity.getPositionEyes(partialTicks);
        final Vec3d vecLook = renderViewEntity.getLook(partialTicks);
        final Vec3d vecExtendedLook = vecPosition.addVector(vecLook.x * blockReachDistance, vecLook.y * blockReachDistance, vecLook.z * blockReachDistance);

        renderViewEntity.posX = posX;
        renderViewEntity.posY = posY;
        renderViewEntity.posZ = posZ;

        return schematic.rayTraceBlocks(vecPosition, vecExtendedLook, false, false, true);
    }
}
