package com.github.lunatrius.schematica.handler.client;

import com.github.lunatrius.schematica.client.gui.control.GuiSchematicControl;
import com.github.lunatrius.schematica.client.gui.load.GuiSchematicLoad;
import com.github.lunatrius.schematica.client.gui.save.GuiSchematicSave;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Names;
import com.mumfrey.liteloader.core.LiteLoader;
import com.github.lunatrius.schematica.util.PickBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.input.Keyboard;

public class InputHandler {
    public static final InputHandler INSTANCE = new InputHandler();

    private static final KeyBinding KEY_BINDING_LOAD = new KeyBinding(Names.Keys.LOAD, Keyboard.KEY_DIVIDE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_SAVE = new KeyBinding(Names.Keys.SAVE, Keyboard.KEY_MULTIPLY, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_CONTROL = new KeyBinding(Names.Keys.CONTROL, Keyboard.KEY_SUBTRACT, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_INC = new KeyBinding(Names.Keys.LAYER_INC, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_DEC = new KeyBinding(Names.Keys.LAYER_DEC, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_LAYER_TOGGLE = new KeyBinding(Names.Keys.LAYER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_RENDER_TOGGLE = new KeyBinding(Names.Keys.RENDER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_PRINTER_TOGGLE = new KeyBinding(Names.Keys.PRINTER_TOGGLE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_MOVE_HERE = new KeyBinding(Names.Keys.MOVE_HERE, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_PICK_BLOCK = new KeyBinding(Names.Keys.PICK_BLOCK, Keyboard.KEY_NONE, Names.Keys.CATEGORY);
    private static final KeyBinding KEY_BINDING_PLACE_BLOCK = new KeyBinding("Place Block", Keyboard.KEY_X, Names.Keys.CATEGORY);

    public static void init() {
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_LOAD);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_SAVE);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_CONTROL);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_LAYER_INC);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_LAYER_DEC);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_LAYER_TOGGLE);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_RENDER_TOGGLE);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_PRINTER_TOGGLE);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_MOVE_HERE);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_PICK_BLOCK);
        LiteLoader.getInput().registerKeyBinding(KEY_BINDING_PLACE_BLOCK);
    }

    public static final KeyBinding[] KEY_BINDINGS = new KeyBinding[]{
            KEY_BINDING_LOAD,
            KEY_BINDING_SAVE,
            KEY_BINDING_CONTROL,
            KEY_BINDING_LAYER_INC,
            KEY_BINDING_LAYER_DEC,
            KEY_BINDING_LAYER_TOGGLE,
            KEY_BINDING_RENDER_TOGGLE,
            KEY_BINDING_PRINTER_TOGGLE,
            KEY_BINDING_MOVE_HERE,
            KEY_BINDING_PICK_BLOCK,
            KEY_BINDING_PLACE_BLOCK
    };

    private final Minecraft minecraft = Minecraft.getMinecraft();

    public static void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (minecraft.currentScreen == null) {
            if (KEY_BINDING_LOAD.isPressed()) {
                minecraft.displayGuiScreen(new GuiSchematicLoad(minecraft.currentScreen));
            }

            if (KEY_BINDING_SAVE.isPressed()) {
                minecraft.displayGuiScreen(new GuiSchematicSave(minecraft.currentScreen));
            }

            if (KEY_BINDING_CONTROL.isPressed()) {
                minecraft.displayGuiScreen(new GuiSchematicControl(minecraft.currentScreen));
            }

            if (KEY_BINDING_LAYER_INC.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null && schematic.isRenderingLayer) {
                    schematic.renderingLayer = MathHelper.clamp(schematic.renderingLayer + 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_LAYER_DEC.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null && schematic.isRenderingLayer) {
                    schematic.renderingLayer = MathHelper.clamp(schematic.renderingLayer - 1, 0, schematic.getHeight() - 1);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_LAYER_TOGGLE.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null) {
                    schematic.isRenderingLayer = !schematic.isRenderingLayer;
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_RENDER_TOGGLE.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null) {
                    schematic.isRendering = !schematic.isRendering;
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_PRINTER_TOGGLE.isPressed()) {
                if (ClientProxy.schematic != null) {
                    final boolean printing = SchematicPrinter.INSTANCE.togglePrinting();
                    minecraft.player.sendMessage(new TextComponentTranslation(Names.Messages.TOGGLE_PRINTER, I18n.format(printing ? Names.Gui.ON : Names.Gui.OFF)));
                }
            }

            if (KEY_BINDING_MOVE_HERE.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null) {
                    ClientProxy.moveSchematicToPlayer(schematic);
                    RenderSchematic.INSTANCE.refresh();
                }
            }

            if (KEY_BINDING_PICK_BLOCK.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null && schematic.isRendering) {
                    pickBlock(minecraft, schematic, ClientProxy.objectMouseOver);
                }
            }

            if (KEY_BINDING_PLACE_BLOCK.isPressed()) {
                final SchematicWorld schematic = ClientProxy.schematic;
                if (schematic != null && schematic.isRendering) {
                    placeBlock(minecraft, schematic, ClientProxy.objectMouseOver, true, SchematicPrinter.INSTANCE);
                }
            }
            SchematicPrinter.INSTANCE.setPlacing(KEY_BINDING_PLACE_BLOCK.isKeyDown());
        }
    }

    private static boolean pickBlock(Minecraft minecraft, final SchematicWorld schematic, final RayTraceResult objectMouseOver) {
        // Minecraft.func_147112_ai
        if (objectMouseOver == null) {
            return false;
        }

        if (objectMouseOver.typeOfHit == RayTraceResult.Type.MISS) {
            return false;
        }

        final EntityPlayerSP player = minecraft.player;
        if (!PickBlock.onPickBlock(objectMouseOver, player, schematic)) {
            return true;
        }

        if (player.capabilities.isCreativeMode) {
            final int slot = player.inventoryContainer.inventorySlots.size() - 10 + player.inventory.currentItem;
            minecraft.playerController.sendSlotPacket(player.inventory.getStackInSlot(player.inventory.currentItem), slot);
            return true;
        }

        return false;
    }

    public static boolean placeBlock(Minecraft minecraft, final WorldClient world, final RayTraceResult objectMouseOver, boolean timeout, SchematicPrinter printer) {
        // Minecraft.func_147112_ai
        if (objectMouseOver == null) {
            return false;
        }

        if (objectMouseOver.typeOfHit == RayTraceResult.Type.MISS) {
            return false;
        }

        final EntityPlayerSP player = minecraft.player;
        PickBlock.onPickBlock(objectMouseOver, player, world);

        if (printer.placeThisBlock(world, player, timeout, objectMouseOver.getBlockPos())) {
            return true;
        }

        if (player.capabilities.isCreativeMode) {
            final int slot = player.inventoryContainer.inventorySlots.size() - 10 + player.inventory.currentItem;
            minecraft.playerController.sendSlotPacket(player.inventory.getStackInSlot(player.inventory.currentItem), slot);
            if (printer.placeThisBlock(world, player, timeout, objectMouseOver.getBlockPos())) {
                return true;
            }
        }

        return false;
    }
}
