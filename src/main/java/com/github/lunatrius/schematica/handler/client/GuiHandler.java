package com.github.lunatrius.schematica.handler.client;

public class GuiHandler {
    public static final GuiHandler INSTANCE = new GuiHandler();

    // Moved to MixinMinecraft.java
//    @SubscribeEvent
//    public void onGuiOpen(final GuiOpenEvent event) {
//        if (SchematicPrinter.INSTANCE.isPrinting()) {
//            if (event.getGui() instanceof GuiEditSign) {
//                event.setGui(null);
//            }
//        }
//    }
}
