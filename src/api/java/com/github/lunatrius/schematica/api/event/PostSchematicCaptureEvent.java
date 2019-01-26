package com.github.lunatrius.schematica.api.event;

import com.github.lunatrius.schematica.api.ISchematic;

/**
 * This event is fired after an ISchematic has been created out of a part of the world.
 * This is an appropriate place to modify the schematic's blocks, metadata and tile entities before they are persisted.
 * Register to this event using MinecraftForge.EVENT_BUS
 */
public class PostSchematicCaptureEvent {
    /**
     * The schematic that was just generated.
     */
    public final ISchematic schematic;

    public PostSchematicCaptureEvent(final ISchematic schematic) {
        this.schematic = schematic;
    }
}
