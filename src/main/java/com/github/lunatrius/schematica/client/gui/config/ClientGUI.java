package com.github.lunatrius.schematica.client.gui.config;

import com.github.lunatrius.schematica.LiteModSchematica;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.reference.Names;

import java.io.File;
import java.util.Arrays;

public class ClientGUI {
    public static ScrollGUI.GuiGameRuleList list;
    private static ScrollGUI.GuiGameRuleList ruleList;
    private ScrollGUI scrollGUI;
    private static int displayLayer;

    public static final int ROOT = 0;
    public static final int HOTBAR_SLOTS_LAYER = 1;
    public static final int AIRBLOCK_LAYER = 2;

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
        if (displayLayer == ROOT) {
            displayRoot();
        } else if (displayLayer == HOTBAR_SLOTS_LAYER) {
            displayHobarSlots();
        } else if (displayLayer == AIRBLOCK_LAYER) {
            displayAirBlockList();
        }
    }

    private static void displayAirBlockList() {
        list.clear();

        list.addNewButton("Add airblock", 0);
        for (int i = 0; i < ConfigurationHandler.extraAirBlocks.length; i++) {
            list.addNewText(Names.Config.EXTRA_AIR_BLOCKS, ConfigurationHandler.extraAirBlocks[i], true, Names.Config.EXTRA_AIR_BLOCKS_DESC, (i + 1));
        }
    }

    private static void displayHobarSlots() {
        list.clear();

        for (int i = 0; i < 9; i++) {
            list.addNewRuleButton(Names.Config.SWAP_SLOT, String.valueOf(ConfigurationHandler.swapSlots[i]), true, Names.Config.SWAP_SLOT_DESC, i);
        }
    }

    private static void displayRoot() {
        list.clear();

        list.addLabel("Printer");
        list.addNewButton(Names.Config.HOTBAR_SLOTS, HOTBAR_SLOTS_ID);
        list.addNewRuleButton(Names.Config.DESTROY_BLOCKS, String.valueOf(ConfigurationHandler.destroyBlocks), true, Names.Config.DESTROY_BLOCKS_DESC, DESTROY_BLOCKS_ID);
        list.addNewRuleButton(Names.Config.DESTROY_INSTANTLY, String.valueOf(ConfigurationHandler.destroyInstantly), true, Names.Config.DESTROY_INSTANTLY_DESC, DESTROY_INSTANTLY_ID);
        list.addNewRuleButton(Names.Config.PLACE_ADJACENT, String.valueOf(ConfigurationHandler.placeAdjacent), true, Names.Config.PLACE_ADJACENT_DESC, PLACE_ADJACENT_ID);
        list.addNewRuleSlider(Names.Config.PLACE_DELAY, true, Names.Config.PLACE_DELAY_DESC, 0, 20, ConfigurationHandler.placeDelay, PLACE_DELAY_ID);
        list.addNewRuleSlider(Names.Config.PLACE_DISTANCE, true, Names.Config.PLACE_DISTANCE_DESC, 0, 5, ConfigurationHandler.placeDistance, PLACE_DISTANCE_ID);
        list.addNewRuleButton(Names.Config.PLACE_INSTANTLY, String.valueOf(ConfigurationHandler.placeInstantly), true, Names.Config.PLACE_INSTANTLY_DESC, PLACE_INSTANTLY_ID);
        list.addNewRuleSlider(Names.Config.TIMEOUT, true, Names.Config.TIMEOUT_DESC, 0, 100, ConfigurationHandler.timeout, TIMEOUT_ID);

        list.addLabel("");
        list.addLabel("Renderer");
        list.addNewRuleSlider(Names.Config.ALPHA, true, Names.Config.ALPHA_DESC, 0f, 1.0f, ConfigurationHandler.alpha, ALPHA_ID);
        list.addNewRuleButton(Names.Config.ALPHA_ENABLED, String.valueOf(ConfigurationHandler.enableAlpha), true, Names.Config.ALPHA_ENABLED_DESC, ALPHA_ENABLED_ID);
        list.addNewRuleSlider(Names.Config.BLOCK_DELTA, true, Names.Config.BLOCK_DELTA_DESC, 0f, 0.2f, ConfigurationHandler.blockDelta, BLOCK_DELTA_ID);
        list.addNewRuleButton(Names.Config.HIGHLIGHT, String.valueOf(ConfigurationHandler.highlight), true, Names.Config.HIGHLIGHT_DESC, HIGHLIGHT_ID);
        list.addNewRuleButton(Names.Config.HIGHLIGHT_AIR, String.valueOf(ConfigurationHandler.highlightAir), true, Names.Config.HIGHLIGHT_AIR_DESC, HIGHLIGHT_AIR_ID);
        list.addNewRuleSlider(Names.Config.RENDER_DISTANCE, true, Names.Config.RENDER_DISTANCE_DESC, 2, 16, ConfigurationHandler.renderDistance, RENDER_DISTANCE_ID);

        list.addLabel("");
        list.addLabel("General");
        list.addNewText(Names.Config.SCHEMATIC_DIRECTORY, ConfigurationHandler.schematicDirectoryStr, true, Names.Config.SCHEMATIC_DIRECTORY_DESC, SCHEMATIC_DIRECTORY_ID);
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
        if (displayLayer == ROOT) {
            optionChangedRoot(id, text, i, f);
        } else if (displayLayer == HOTBAR_SLOTS_LAYER) {
            optionChangedHotbar(id, text, i, f);
        } else if (displayLayer == AIRBLOCK_LAYER) {
            optionChangedAirblocks(id, text, i, f);
        }

        display();
        ConfigurationHandler.save();
    }

    private void optionChangedAirblocks(int id, String text, int i, float f) {
        if (id == 0) {
            int size = ConfigurationHandler.extraAirBlocks.length + 1;
            ConfigurationHandler.extraAirBlocks = Arrays.copyOf(ConfigurationHandler.extraAirBlocks, size);

            for(int iter = 0; iter < ConfigurationHandler.extraAirBlocks.length; iter++){
                if(ConfigurationHandler.extraAirBlocks[iter] == null){
                    ConfigurationHandler.extraAirBlocks[iter] = new String();
                }
            }

            return;
        }
        ConfigurationHandler.extraAirBlocks[id - 1] = text;
    }

    private void optionChangedHotbar(int id, String text, int i, float f) {
        ConfigurationHandler.swapSlots[id] = !ConfigurationHandler.swapSlots[id];
    }

    private void optionChangedRoot(int id, String text, int i, float f) {
        if (HOTBAR_SLOTS_ID == id) {
            displayLayer = HOTBAR_SLOTS_LAYER;
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
            ConfigurationHandler.schematicDirectoryStr = text;
            ConfigurationHandler.schematicDirectory = new File(LiteModSchematica.proxy.getDataDirectory(), text);
        } else if (EXTRA_AIR_BLOCKS_ID == id) {
            displayLayer = AIRBLOCK_LAYER;
        }
    }

    public void resetButton(int id) {
        if (displayLayer == ROOT) {
            resetRoot(id);
        } else if (displayLayer == HOTBAR_SLOTS_LAYER) {
            resetHotbar(id);
        } else if (displayLayer == AIRBLOCK_LAYER) {
            resetAirblock(id);
        }

        display();
        ConfigurationHandler.save();
    }

    private void resetAirblock(int id) {
        int size = ConfigurationHandler.extraAirBlocks.length;
        String[] newList = new String[size - 1];

        id--;
        int skip = 0;
        for (int i = 0; i < ConfigurationHandler.extraAirBlocks.length; i++) {
            if (i != id) {
                newList[i - skip] = ConfigurationHandler.extraAirBlocks[i];
            } else {
                skip = 1;
            }
        }

        ConfigurationHandler.extraAirBlocks = newList;
    }

    private void resetHotbar(int id) {
        ConfigurationHandler.swapSlots[id] = ConfigurationHandler.SWAP_SLOTS_DEFAULT[id];
    }

    private void resetRoot(int id) {
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
            ConfigurationHandler.schematicDirectoryStr = ConfigurationHandler.SCHEMATIC_DIRECTORY_STR;
            ConfigurationHandler.schematicDirectory = new File(LiteModSchematica.proxy.getDataDirectory(), ConfigurationHandler.schematicDirectoryStr);
        } else if (EXTRA_AIR_BLOCKS_ID == id) {
        }
    }

    /**
     * Returns the GUI list that is generated for rendering.
     *
     * @return The list of GUI buttons generated based on the level of display.
     */
    public ScrollGUI.GuiGameRuleList getList() {
        return list;
    }
}
