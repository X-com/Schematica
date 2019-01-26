package com.github.lunatrius.schematica.handler.client;

import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public class TickHandler {
    public static final TickHandler INSTANCE = new TickHandler();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private int ticks = -1;

    private TickHandler() {
    }

    // Forge methods, can probably remove
//    @SubscribeEvent
//    public void onClientConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
//        /* TODO: is this still needed?
//        Reference.logger.info("Scheduling client settings reset.");
//        ClientProxy.isPendingReset = true;
//        */
//    }
//
//    @SubscribeEvent
//    public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
//        Reference.logger.info("Scheduling client settings reset.");
//        ClientProxy.isPendingReset = true;
//    }

    public void onClientTick() {
        if (this.minecraft.isGamePaused()) {
            return;
        }

        this.minecraft.mcProfiler.startSection("schematica");
        final WorldClient world = this.minecraft.world;
        final EntityPlayerSP player = this.minecraft.player;
        final SchematicWorld schematic = ClientProxy.schematic;
        if (world != null && player != null && schematic != null && schematic.isRendering) {
            this.minecraft.mcProfiler.startSection("printer");
            final SchematicPrinter printer = SchematicPrinter.INSTANCE;
            if (printer.isEnabled() && printer.isPrinting() && this.ticks-- < 0) {
                this.ticks = ConfigurationHandler.placeDelay;

                printer.print(world, player);
            }
            if (printer.isEnabled() && printer.isPlacing() && this.ticks-- < 0) {
                this.ticks = ConfigurationHandler.placeDelay;

                printer.blockPlacer(world, player, true);
            }

            this.minecraft.mcProfiler.endSection();
        }

        if (ClientProxy.isPendingReset) {
            Schematica.proxy.resetSettings();
            ClientProxy.isPendingReset = false;
            Reference.logger.info("Client settings have been reset.");
        }

        this.minecraft.mcProfiler.endSection();
    }
}
