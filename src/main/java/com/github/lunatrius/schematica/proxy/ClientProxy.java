package com.github.lunatrius.schematica.proxy;

import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.util.ISchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.schematic.SchematicFormat;
import lunatriuscore.MBlockPos;
import lunatriuscore.vector.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;

import java.io.File;
import java.io.IOException;

public class ClientProxy extends CommonProxy {
    public static boolean isRenderingGuide = false;
    public static boolean isPendingReset = false;

    public static final Vector3d playerPosition = new Vector3d();
    public static EnumFacing orientation = null;
    public static int rotationRender = 0;

    public static SchematicWorld schematic = null;

    public static final MBlockPos pointA = new MBlockPos();
    public static final MBlockPos pointB = new MBlockPos();
    public static final MBlockPos pointMin = new MBlockPos();
    public static final MBlockPos pointMax = new MBlockPos();

    public static EnumFacing axisFlip = EnumFacing.UP;
    public static EnumFacing axisRotation = EnumFacing.UP;

    public static RayTraceResult objectMouseOver = null;

    private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

    public static void setPlayerData(final EntityPlayer player, final float partialTicks) {
        playerPosition.x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        playerPosition.y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        playerPosition.z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        orientation = getOrientation(player);

        rotationRender = MathHelper.floor(player.rotationYaw / 90) & 3;
    }

    private static EnumFacing getOrientation(final EntityPlayer player) {
        if (player.rotationPitch > 45) {
            return EnumFacing.DOWN;
        } else if (player.rotationPitch < -45) {
            return EnumFacing.UP;
        } else {
            switch (MathHelper.floor(player.rotationYaw / 90.0 + 0.5) & 3) {
                case 0:
                    return EnumFacing.SOUTH;
                case 1:
                    return EnumFacing.WEST;
                case 2:
                    return EnumFacing.NORTH;
                case 3:
                    return EnumFacing.EAST;
            }
        }

        return null;
    }

    public static void updatePoints() {
        pointMin.xx = Math.min(pointA.xx, pointB.xx);
        pointMin.yy = Math.min(pointA.yy, pointB.yy);
        pointMin.zz = Math.min(pointA.zz, pointB.zz);

        pointMax.xx = Math.max(pointA.xx, pointB.xx);
        pointMax.yy = Math.max(pointA.yy, pointB.yy);
        pointMax.zz = Math.max(pointA.zz, pointB.zz);
    }

    public static void movePointToPlayer(final MBlockPos point) {
        point.xx = (int) Math.floor(playerPosition.x);
        point.yy = (int) Math.floor(playerPosition.y);
        point.zz = (int) Math.floor(playerPosition.z);

        switch (rotationRender) {
            case 0:
                point.xx -= 1;
                point.zz += 1;
                break;
            case 1:
                point.xx -= 1;
                point.zz -= 1;
                break;
            case 2:
                point.xx += 1;
                point.zz -= 1;
                break;
            case 3:
                point.xx += 1;
                point.zz += 1;
                break;
        }
    }

    public static void moveSchematicToPlayer(final SchematicWorld schematic) {
        if (schematic != null) {
            final MBlockPos position = schematic.position;
            position.xx = (int) Math.floor(playerPosition.x);
            position.yy = (int) Math.floor(playerPosition.y);
            position.zz = (int) Math.floor(playerPosition.z);

            switch (rotationRender) {
                case 0:
                    position.xx -= schematic.getWidth();
                    position.zz += 1;
                    break;
                case 1:
                    position.xx -= schematic.getWidth();
                    position.zz -= schematic.getLength();
                    break;
                case 2:
                    position.xx += 1;
                    position.zz -= schematic.getLength();
                    break;
                case 3:
                    position.xx += 1;
                    position.zz += 1;
                    break;
            }
        }
    }

//    @Override
//    public void preInit(final FMLPreInitializationEvent event) {
//        super.preInit(event);
//
//        final Property[] sliders = {
//                ConfigurationHandler.propAlpha,
//                ConfigurationHandler.propBlockDelta,
//                ConfigurationHandler.propRenderDistance,
//                ConfigurationHandler.propPlaceDelay,
//                ConfigurationHandler.propTimeout,
//                ConfigurationHandler.propPlaceDistance
//        };
//        for (final Property prop : sliders) {
//            prop.setConfigEntryClass(GuiConfigEntries.NumberSliderEntry.class);
//        }
//
//        for (final KeyBinding keyBinding : InputHandler.KEY_BINDINGS) {
//            ClientRegistry.registerKeyBinding(keyBinding);
//        }
//    }
//
//    @Override
//    public void init(final FMLInitializationEvent event) {
//        super.init(event);
//
//        MinecraftForge.EVENT_BUS.register(InputHandler.INSTANCE);
//        MinecraftForge.EVENT_BUS.register(TickHandler.INSTANCE);
//        MinecraftForge.EVENT_BUS.register(RenderTickHandler.INSTANCE);
//        MinecraftForge.EVENT_BUS.register(ConfigurationHandler.INSTANCE);
//        MinecraftForge.EVENT_BUS.register(RenderSchematic.INSTANCE);
//        MinecraftForge.EVENT_BUS.register(GuiHandler.INSTANCE);
//        MinecraftForge.EVENT_BUS.register(new OverlayHandler());
//        MinecraftForge.EVENT_BUS.register(new WorldHandler());
//
//        ClientCommandHandler.instance.registerCommand(new CommandSchematicaReplace());
//    }

    public void postInit() {
//        super.postInit(event);

        resetSettings();
    }

    @Override
    public File getDataDirectory() {
        final File file = MINECRAFT.mcDataDir;
        try {
            return file.getCanonicalFile();
        } catch (final IOException e) {
            Reference.logger.debug("Could not canonize path!", e);
        }
        return file;
    }

    @Override
    public void resetSettings() {
        super.resetSettings();

        SchematicPrinter.INSTANCE.setEnabled(true);
        unloadSchematic();

        isRenderingGuide = false;

        playerPosition.set(0, 0, 0);
        orientation = null;
        rotationRender = 0;

        pointA.set(0, 0, 0);
        pointB.set(0, 0, 0);
        updatePoints();
    }

    @Override
    public void unloadSchematic() {
        schematic = null;
        RenderSchematic.INSTANCE.setWorldAndLoadRenderers(null);
        SchematicPrinter.INSTANCE.setSchematic(null);
    }

    @Override
    public boolean loadSchematic(final EntityPlayer player, final File directory, final String filename) {
        final ISchematic schematic = SchematicFormat.readFromFile(directory, filename);
        if (schematic == null) {
            return false;
        }

        final SchematicWorld world = new SchematicWorld(schematic);

        Reference.logger.debug("Loaded {} [w:{},h:{},l:{}]", filename, world.getWidth(), world.getHeight(), world.getLength());

        ClientProxy.schematic = world;
        RenderSchematic.INSTANCE.setWorldAndLoadRenderers(world);
        SchematicPrinter.INSTANCE.setSchematic(world);
        world.isRendering = true;

        return true;
    }

    @Override
    public boolean isPlayerQuotaExceeded(final EntityPlayer player) {
        return false;
    }

    @Override
    public File getPlayerSchematicDirectory(final EntityPlayer player, final boolean privateDirectory) {
        return ConfigurationHandler.schematicDirectory;
    }
}
