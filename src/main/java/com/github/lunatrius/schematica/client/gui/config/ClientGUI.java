package com.github.lunatrius.schematica.client.gui.config;

import com.github.lunatrius.schematica.LiteModSchematica;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.reference.Names;

import java.io.File;

public class ClientGUI {
    public static ScrollGUI.GuiGameRuleList list;
    private static ScrollGUI.GuiGameRuleList ruleList;
    private ScrollGUI scrollGUI;
    private static int displayLayer;

    public static final int ROOT = 0;

    public static final int HOTBAR_SLOTS_ID = 0;
    public static final int DESTROY_BLOCKS_ID = 1;
    public static final int DESTROY_INSTANTLY_ID = 2;
    public static final int PLACE_ADJACENT_ID = 3;
    public static final int PLACE_DELAY_ID = 4;
    public static final int PLACE_DISTANCE_ID = 5;
    public static final int PLACE_INSTANTLY_ID = 6;
    public static final int TIMEOUT_ID = 7;

    public static final int ALPHA_ID = 8;
    public static final int ALPHA_ENABLED_ID = 9;
    public static final int BLOCK_DELTA_ID = 10;
    public static final int HIGHLIGHT_ID = 11;
    public static final int HIGHLIGHT_AIR_ID = 12;
    public static final int RENDER_DISTANCE_ID = 13;

    public static final int SCHEMATIC_DIRECTORY_ID = 14;
    public static final int EXTRA_AIR_BLOCKS_ID = 15;


    public ClientGUI(ScrollGUI scrollGUI) {
        this.scrollGUI = scrollGUI;
    }

    /**
     * Static update method to update the different display options.
     */
    public static void display() {
        list.clear();

        list.addLabel("Printer");
        list.addNewButton(Names.Config.HOTBAR_SLOTS, HOTBAR_SLOTS_ID);
        list.addNewRuleButton(Names.Config.DESTROY_BLOCKS, String.valueOf(ConfigurationHandler.destroyBlocks), true, Names.Config.DESTROY_BLOCKS_DESC, DESTROY_BLOCKS_ID);
        list.addNewRuleButton(Names.Config.DESTROY_INSTANTLY, String.valueOf(ConfigurationHandler.destroyInstantly), true, Names.Config.DESTROY_INSTANTLY_DESC, DESTROY_INSTANTLY_ID);
        list.addNewRuleButton(Names.Config.PLACE_ADJACENT, String.valueOf(ConfigurationHandler.placeAdjacent), true, Names.Config.PLACE_ADJACENT_DESC, PLACE_ADJACENT_ID);
        list.addNewRuleSlider(Names.Config.PLACE_DELAY, false, Names.Config.PLACE_DELAY_DESC, 0, 20, 1, PLACE_DELAY_ID);
        list.addNewRuleSlider(Names.Config.PLACE_DISTANCE, false, Names.Config.PLACE_DISTANCE_DESC, 0, 5, 5, PLACE_DISTANCE_ID);
        list.addNewRuleButton(Names.Config.PLACE_INSTANTLY, String.valueOf(ConfigurationHandler.placeInstantly), true, Names.Config.PLACE_INSTANTLY_DESC, PLACE_INSTANTLY_ID);
        list.addNewRuleSlider(Names.Config.TIMEOUT, false, Names.Config.TIMEOUT_DESC, 0, 100, 10, TIMEOUT_ID);

        list.addLabel("");
        list.addLabel("Renderer");
        list.addNewRuleSlider(Names.Config.ALPHA, false, Names.Config.ALPHA_DESC, 0f, 1.0f, 0, ALPHA_ID);
        list.addNewRuleButton(Names.Config.ALPHA_ENABLED, String.valueOf(ConfigurationHandler.enableAlpha), true, Names.Config.ALPHA_ENABLED_DESC, ALPHA_ENABLED_ID);
        list.addNewRuleSlider(Names.Config.BLOCK_DELTA, false, Names.Config.BLOCK_DELTA_DESC, 0f, 0.2f, 0.005f, BLOCK_DELTA_ID);
        list.addNewRuleButton(Names.Config.HIGHLIGHT, String.valueOf(ConfigurationHandler.highlight), true, Names.Config.HIGHLIGHT_DESC, HIGHLIGHT_ID);
        list.addNewRuleButton(Names.Config.HIGHLIGHT_AIR, String.valueOf(ConfigurationHandler.highlightAir), true, Names.Config.HIGHLIGHT_AIR_DESC, HIGHLIGHT_AIR_ID);
        list.addNewRuleSlider(Names.Config.RENDER_DISTANCE, false, Names.Config.RENDER_DISTANCE_DESC, 2, 16, 8, RENDER_DISTANCE_ID);

        list.addLabel("");
        list.addLabel("General");
        list.addNewText(Names.Config.SCHEMATIC_DIRECTORY, ConfigurationHandler.SCHEMATIC_DIRECTORY_STR, true, Names.Config.SCHEMATIC_DIRECTORY_DESC, SCHEMATIC_DIRECTORY_ID);
        list.addNewButton(Names.Config.EXTRA_AIR_BLOCKS, EXTRA_AIR_BLOCKS_ID);
    }

    /**
     * The display level that is being displayed.
     *
     * @return Returns the display level.
     */
    public boolean isRootElseDropTo() {
        if (displayLayer == ROOT) {
            return true;
        }
        displayLayer = ROOT;
        display();
        return false;
    }

    public void optionChanged(int id, String text, int i, float f) {
        if (HOTBAR_SLOTS_ID == id) {
        } else if (DESTROY_BLOCKS_ID == id) {
            ConfigurationHandler.destroyBlocks = !ConfigurationHandler.destroyBlocks;
        } else if (DESTROY_INSTANTLY_ID == id) {
            ConfigurationHandler.destroyInstantly = !ConfigurationHandler.destroyInstantly;
        } else if (PLACE_ADJACENT_ID == id) {
            ConfigurationHandler.placeAdjacent = !ConfigurationHandler.placeAdjacent;
        } else if (PLACE_DELAY_ID == id) {
            ConfigurationHandler.placeDelay = i;
        } else if (PLACE_DISTANCE_ID == id) {
            ConfigurationHandler.placeDistance = i;
        } else if (PLACE_INSTANTLY_ID == id) {
            ConfigurationHandler.placeInstantly = !ConfigurationHandler.placeInstantly;
        } else if (TIMEOUT_ID == id) {
            ConfigurationHandler.timeout = i;
        } else if (ALPHA_ID == id) {
            ConfigurationHandler.alpha = f;
        } else if (ALPHA_ENABLED_ID == id) {
            ConfigurationHandler.enableAlpha = !ConfigurationHandler.enableAlpha;
        } else if (BLOCK_DELTA_ID == id) {
            ConfigurationHandler.blockDelta = f;
        } else if (HIGHLIGHT_ID == id) {
            ConfigurationHandler.highlight = !ConfigurationHandler.highlight;
        } else if (HIGHLIGHT_AIR_ID == id) {
            ConfigurationHandler.highlightAir = !ConfigurationHandler.highlightAir;
        } else if (RENDER_DISTANCE_ID == id) {
            ConfigurationHandler.renderDistance = i;
        } else if (SCHEMATIC_DIRECTORY_ID == id) {
            ConfigurationHandler.schematicDirectory = new File(LiteModSchematica.proxy.getDataDirectory(), text);
        } else if (EXTRA_AIR_BLOCKS_ID == id) {
        }

        display();
        ConfigurationHandler.save();
    }

    public void resetButton(int id) {
        if (HOTBAR_SLOTS_ID == id) {
        } else if (DESTROY_BLOCKS_ID == id) {
            ConfigurationHandler.destroyBlocks = ConfigurationHandler.DESTROY_BLOCKS_DEFAULT;
        } else if (DESTROY_INSTANTLY_ID == id) {
            ConfigurationHandler.destroyInstantly = ConfigurationHandler.DESTROY_INSTANTLY_DEFAULT;
        } else if (PLACE_ADJACENT_ID == id) {
            ConfigurationHandler.placeAdjacent = ConfigurationHandler.PLACE_ADJACENT_DEFAULT;
        } else if (PLACE_DELAY_ID == id) {
            ConfigurationHandler.placeDelay = ConfigurationHandler.PLACE_DELAY_DEFAULT;
        } else if (PLACE_DISTANCE_ID == id) {
            ConfigurationHandler.placeDistance = ConfigurationHandler.PLACE_DISTANCE_DEFAULT;
        } else if (PLACE_INSTANTLY_ID == id) {
            ConfigurationHandler.placeInstantly = ConfigurationHandler.PLACE_INSTANTLY_DEFAULT;
        } else if (TIMEOUT_ID == id) {
            ConfigurationHandler.timeout = ConfigurationHandler.TIMEOUT_DEFAULT;
        } else if (ALPHA_ID == id) {
            ConfigurationHandler.alpha = ConfigurationHandler.ALPHA_DEFAULT;
        } else if (ALPHA_ENABLED_ID == id) {
            ConfigurationHandler.enableAlpha = ConfigurationHandler.ENABLE_ALPHA_DEFAULT;
        } else if (BLOCK_DELTA_ID == id) {
            ConfigurationHandler.blockDelta = ConfigurationHandler.BLOCK_DELTA_DEFAULT;
        } else if (HIGHLIGHT_ID == id) {
            ConfigurationHandler.highlight = ConfigurationHandler.HIGHLIGHT_DEFAULT;
        } else if (HIGHLIGHT_AIR_ID == id) {
            ConfigurationHandler.highlightAir = ConfigurationHandler.HIGHLIGHT_AIR_DEFAULT;
        } else if (RENDER_DISTANCE_ID == id) {
            ConfigurationHandler.renderDistance = ConfigurationHandler.RENDER_DISTANCE_DEFAULT;
        } else if (SCHEMATIC_DIRECTORY_ID == id) {
            ConfigurationHandler.schematicDirectory = ConfigurationHandler.SCHEMATIC_DIRECTORY_DEFAULT;
        } else if (EXTRA_AIR_BLOCKS_ID == id) {
        }

        display();
        ConfigurationHandler.save();
    }

        /**
         * Returns the GUI list that is generated for rendering.
         *
         * @return The list of GUI buttons generated based on the level of display.
         */
        public ScrollGUI.GuiGameRuleList getList () {
            return list;
        }
    }
