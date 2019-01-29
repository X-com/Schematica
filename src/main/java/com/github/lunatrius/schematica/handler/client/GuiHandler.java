package com.github.lunatrius.schematica.handler.client;

import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiEditSign;

public class GuiHandler {
    public static final GuiHandler INSTANCE = new GuiHandler();

    public static Gui onGuiOpen(final Gui gui) {
        if (SchematicPrinter.INSTANCE.isPrinting()) {
            if (gui instanceof GuiEditSign) {
                return null;
            }
        }
        return gui;
    }
}
