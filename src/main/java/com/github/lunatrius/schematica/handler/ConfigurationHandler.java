package com.github.lunatrius.schematica.handler;

import com.github.lunatrius.schematica.LiteModSchematica;
import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ConfigurationHandler {
    public static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    public static final boolean DUMP_BLOCK_LIST_DEFAULT = false;
    public static final boolean SHOW_DEBUG_INFO_DEFAULT = true;
    public static final boolean ENABLE_ALPHA_DEFAULT = false;
    public static final float ALPHA_DEFAULT = 1.0f;
    public static final boolean HIGHLIGHT_DEFAULT = true;
    public static final boolean HIGHLIGHT_AIR_DEFAULT = true;
    public static final float BLOCK_DELTA_DEFAULT = 0.005f;
    public static final int RENDER_DISTANCE_DEFAULT = 8;
    public static final int PLACE_DELAY_DEFAULT = 1;
    public static final int TIMEOUT_DEFAULT = 10;
    public static final int PLACE_DISTANCE_DEFAULT = 5;
    public static final boolean PLACE_INSTANTLY_DEFAULT = false;
    public static final boolean DESTROY_BLOCKS_DEFAULT = false;
    public static final boolean DESTROY_INSTANTLY_DEFAULT = false;
    public static final boolean PLACE_ADJACENT_DEFAULT = true;
    public static final boolean[] SWAP_SLOTS_DEFAULT = new boolean[] {
            false, false, false, false, false, true, true, true, true
    };
    public static final String SCHEMATIC_DIRECTORY_STR = "./schematics";
    public static final File SCHEMATIC_DIRECTORY_DEFAULT = new File(LiteModSchematica.proxy.getDataDirectory(), SCHEMATIC_DIRECTORY_STR);
    public static final String[] EXTRA_AIR_BLOCKS_DEFAULT = {};
    public static final String SORT_TYPE_DEFAULT = "";

    public static boolean dumpBlockList = DUMP_BLOCK_LIST_DEFAULT;
    public static boolean showDebugInfo = SHOW_DEBUG_INFO_DEFAULT;
    public static boolean enableAlpha = ENABLE_ALPHA_DEFAULT;
    public static float alpha = (float) ALPHA_DEFAULT;
    public static boolean highlight = HIGHLIGHT_DEFAULT;
    public static boolean highlightAir = HIGHLIGHT_AIR_DEFAULT;
    public static float blockDelta = (float) BLOCK_DELTA_DEFAULT;
    public static int renderDistance = RENDER_DISTANCE_DEFAULT;
    public static int placeDelay = PLACE_DELAY_DEFAULT;
    public static int timeout = TIMEOUT_DEFAULT;
    public static int placeDistance = PLACE_DISTANCE_DEFAULT;
    public static boolean placeInstantly = PLACE_INSTANTLY_DEFAULT;
    public static boolean destroyBlocks = DESTROY_BLOCKS_DEFAULT;
    public static boolean destroyInstantly = DESTROY_INSTANTLY_DEFAULT;
    public static boolean placeAdjacent = PLACE_ADJACENT_DEFAULT;
    public static boolean[] swapSlots = Arrays.copyOf(SWAP_SLOTS_DEFAULT, SWAP_SLOTS_DEFAULT.length);
    public static final Queue<Integer> swapSlotsQueue = new ArrayDeque<Integer>();
    public static File schematicDirectory = SCHEMATIC_DIRECTORY_DEFAULT;
    public static String[] extraAirBlocks = Arrays.copyOf(EXTRA_AIR_BLOCKS_DEFAULT, EXTRA_AIR_BLOCKS_DEFAULT.length);
    public static String sortType = SORT_TYPE_DEFAULT;

    private static final Set<Block> extraAirBlockList = new HashSet<Block>();

    private static void populateExtraAirBlocks() {
        extraAirBlockList.clear();
        for (final String name : extraAirBlocks) {
            final Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
            if (block != Blocks.AIR) {
                extraAirBlockList.add(block);
            }
        }
    }

    public static boolean isExtraAirBlock(final Block block) {
        return extraAirBlockList.contains(block);
    }

    public static void save() {
        String file = "schematica.cfg";
        JsonObject obj = new JsonObject();
        Gson gson = new Gson();

        obj.addProperty("dumpBlockList", dumpBlockList);
        obj.addProperty("showDebugInfo", showDebugInfo);
        obj.addProperty("enableAlpha", enableAlpha);
        obj.addProperty("alpha", alpha);
        obj.addProperty("highlight", highlight);
        obj.addProperty("highlightAir", highlightAir);
        obj.addProperty("blockDelta", blockDelta);
        obj.addProperty("renderDistance", renderDistance);
        obj.addProperty("placeDelay", placeDelay);
        obj.addProperty("timeout", timeout);
        obj.addProperty("placeDistance", placeDistance);
        obj.addProperty("placeInstantly", placeInstantly);
        obj.addProperty("destroyBlocks", destroyBlocks);
        obj.addProperty("destroyInstantly", destroyInstantly);
        obj.addProperty("placeAdjacent", placeAdjacent);
        obj.addProperty("schematicDirectory", schematicDirectory.getPath());
        obj.addProperty("sortType", sortType);

        obj.add("swapSlots", gson.toJsonTree(swapSlots));
        obj.add("extraAirBlocks", gson.toJsonTree(extraAirBlocks));

        try {
            FileWriter writer = new FileWriter(file);
            writer.write((new GsonBuilder().setPrettyPrinting().create()).toJson(obj));
            writer.close();
        } catch (IOException e) {
        }
    }

    public static void load() {
        String file = "schematica.cfg";
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        String json = "";
        try {
            json = new String(Files.readAllBytes(Paths.get(file)));
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            return;
        }

        try {
            JsonElement jsonTree = jsonParser.parse(json);
            if (jsonTree.isJsonObject()) {
                JsonObject jsonObject = jsonTree.getAsJsonObject();

                dumpBlockList = jsonObject.get("dumpBlockList").getAsBoolean();
                showDebugInfo = jsonObject.get("showDebugInfo").getAsBoolean();
                enableAlpha = jsonObject.get("enableAlpha").getAsBoolean();
                alpha = jsonObject.get("alpha").getAsFloat();
                highlight = jsonObject.get("highlight").getAsBoolean();
                highlightAir = jsonObject.get("highlightAir").getAsBoolean();
                blockDelta = jsonObject.get("blockDelta").getAsFloat();
                renderDistance = jsonObject.get("renderDistance").getAsInt();
                placeDelay = jsonObject.get("placeDelay").getAsInt();
                timeout = jsonObject.get("timeout").getAsInt();
                placeDistance = jsonObject.get("placeDistance").getAsInt();
                placeInstantly = jsonObject.get("placeInstantly").getAsBoolean();
                destroyBlocks = jsonObject.get("destroyBlocks").getAsBoolean();
                destroyInstantly = jsonObject.get("destroyInstantly").getAsBoolean();
                placeAdjacent = jsonObject.get("placeAdjacent").getAsBoolean();
                schematicDirectory = new File(LiteModSchematica.proxy.getDataDirectory(), jsonObject.get("schematicDirectory").getAsString());
                sortType = jsonObject.get("sortType").getAsString();

                swapSlots = gson.fromJson(jsonObject.get("swapSlots"), boolean[].class);
                extraAirBlocks = gson.fromJson(jsonObject.get("extraAirBlocks"), String[].class);

                populateExtraAirBlocks();
            }
        } catch (Exception e) {
        }
    }
}
