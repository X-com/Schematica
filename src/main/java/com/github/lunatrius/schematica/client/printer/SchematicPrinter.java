package com.github.lunatrius.schematica.client.printer;

import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.printer.nbtsync.NBTSync;
import com.github.lunatrius.schematica.client.printer.nbtsync.SyncRegistry;
import com.github.lunatrius.schematica.client.printer.registry.PlacementData;
import com.github.lunatrius.schematica.client.printer.registry.PlacementRegistry;
import com.github.lunatrius.schematica.client.util.BlockStateToItemStack;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.handler.client.InputHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Constants;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SchematicPrinter {
    public static final SchematicPrinter INSTANCE = new SchematicPrinter();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private boolean isEnabled = true;
    private boolean isPrinting = false;
    private boolean isPlacing = false;

    private SchematicWorld schematic = null;
    private byte[][][] timeout = null;
    private HashMap<BlockPos, Integer> syncBlacklist = new HashMap<BlockPos, Integer>();

    private static Block[] checkListDirectional = {
            Blocks.PISTON,
            Blocks.STICKY_PISTON,
            Blocks.OBSERVER,
            Blocks.DISPENSER,
            Blocks.DROPPER,
    };
    private static Block[] checkListHorizontal = {
            Blocks.UNPOWERED_COMPARATOR,
            Blocks.UNPOWERED_REPEATER,
            Blocks.BLACK_GLAZED_TERRACOTTA,
            Blocks.WHITE_GLAZED_TERRACOTTA,
            Blocks.ORANGE_GLAZED_TERRACOTTA,
            Blocks.MAGENTA_GLAZED_TERRACOTTA,
            Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Blocks.YELLOW_GLAZED_TERRACOTTA,
            Blocks.LIME_GLAZED_TERRACOTTA,
            Blocks.PINK_GLAZED_TERRACOTTA,
            Blocks.GRAY_GLAZED_TERRACOTTA,
            Blocks.SILVER_GLAZED_TERRACOTTA,
            Blocks.CYAN_GLAZED_TERRACOTTA,
            Blocks.PURPLE_GLAZED_TERRACOTTA,
            Blocks.BLUE_GLAZED_TERRACOTTA,
            Blocks.BROWN_GLAZED_TERRACOTTA,
            Blocks.GREEN_GLAZED_TERRACOTTA,
            Blocks.RED_GLAZED_TERRACOTTA,
            Blocks.BLACK_GLAZED_TERRACOTTA,
    };
    private static Block[] checkListStairTrap = {
            Blocks.OAK_STAIRS,
            Blocks.STONE_STAIRS,
            Blocks.BRICK_STAIRS,
            Blocks.STONE_BRICK_STAIRS,
            Blocks.NETHER_BRICK_STAIRS,
            Blocks.SANDSTONE_STAIRS,
            Blocks.SPRUCE_STAIRS,
            Blocks.BIRCH_STAIRS,
            Blocks.JUNGLE_STAIRS,
            Blocks.QUARTZ_STAIRS,
            Blocks.ACACIA_STAIRS,
            Blocks.DARK_OAK_STAIRS,
            Blocks.RED_SANDSTONE_STAIRS,
            Blocks.PURPUR_STAIRS,
            Blocks.TRAPDOOR,
            Blocks.IRON_TRAPDOOR,
    };
    public static boolean toggleAccuratePlacement;

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean togglePrinting() {
        this.isPrinting = !this.isPrinting && this.schematic != null;
        return this.isPrinting;
    }

    public boolean isPrinting() {
        return this.isPrinting;
    }

    public void setPlacing(boolean place){
        this.isPlacing = place;
    }
    
    public boolean isPlacing(){
        return this.isPlacing;
    }

    public void setPrinting(final boolean isPrinting) {
        this.isPrinting = isPrinting;
    }

    public SchematicWorld getSchematic() {
        return this.schematic;
    }

    public void setSchematic(final SchematicWorld schematic) {
        this.isPrinting = false;
        this.schematic = schematic;
        refresh();
    }

    public void refresh() {
        if (this.schematic != null) {
            this.timeout = new byte[this.schematic.getWidth()][this.schematic.getHeight()][this.schematic.getLength()];
        } else {
            this.timeout = null;
        }
        this.syncBlacklist.clear();
    }

    public boolean print(final WorldClient world, final EntityPlayerSP player) {
        final double dX = ClientProxy.playerPosition.x - this.schematic.position.x;
        final double dY = ClientProxy.playerPosition.y - this.schematic.position.y;
        final double dZ = ClientProxy.playerPosition.z - this.schematic.position.z;
        final int x = (int) Math.floor(dX);
        final int y = (int) Math.floor(dY);
        final int z = (int) Math.floor(dZ);
        final int range = ConfigurationHandler.placeDistance;

        final int minX = Math.max(0, x - range);
        final int maxX = Math.min(this.schematic.getWidth() - 1, x + range);
        int minY = Math.max(0, y - range);
        int maxY = Math.min(this.schematic.getHeight() - 1, y + range);
        final int minZ = Math.max(0, z - range);
        final int maxZ = Math.min(this.schematic.getLength() - 1, z + range);

        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return false;
        }

        final int slot = player.inventory.currentItem;
        final boolean isSneaking = player.isSneaking();

        final boolean isRenderingLayer = this.schematic.isRenderingLayer;
        final int renderingLayer = this.schematic.renderingLayer;

        if (isRenderingLayer) {
            if (renderingLayer > maxY || renderingLayer < minY) {
                return false;
            }

            minY = maxY = renderingLayer;
        }

        syncSneaking(player, true);

        final double blockReachDistance = this.minecraft.playerController.getBlockReachDistance() - 0.1;
        final double blockReachDistanceSq = blockReachDistance * blockReachDistance;
        for (final MBlockPos pos : BlockPosHelper.getAllInBoxXZY(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (pos.distanceSqToCenter(dX, dY, dZ) > blockReachDistanceSq) {
                continue;
            }

            try {
                if (placeBlock(world, player, pos, true)) {
                    return syncSlotAndSneaking(player, slot, isSneaking, true);
                }
            } catch (final Exception e) {
                Reference.logger.error("Could not place block!", e);
                return syncSlotAndSneaking(player, slot, isSneaking, false);
            }
        }

        return syncSlotAndSneaking(player, slot, isSneaking, true);
    }

    private boolean syncSlotAndSneaking(final EntityPlayerSP player, final int slot, final boolean isSneaking, final boolean success) {
        player.inventory.currentItem = slot;
        syncSneaking(player, isSneaking);
        return success;
    }

    private boolean placeBlock(final WorldClient world, final EntityPlayerSP player, final BlockPos pos, boolean timeout) {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        if (this.timeout[x][y][z] > 0 && timeout) {
            this.timeout[x][y][z]--;
            return false;
        }

        final int wx = this.schematic.position.x + x;
        final int wy = this.schematic.position.y + y;
        final int wz = this.schematic.position.z + z;
        final BlockPos realPos = new BlockPos(wx, wy, wz);

        final IBlockState blockState = this.schematic.getBlockState(pos);
        final IBlockState realBlockState = world.getBlockState(realPos);
        final Block realBlock = realBlockState.getBlock();

        if (BlockStateHelper.areBlockStatesEqual(blockState, realBlockState)) {
            // TODO: clean up this mess
            final NBTSync handler = SyncRegistry.INSTANCE.getHandler(realBlock);
            if (handler != null) {
                this.timeout[x][y][z] = (byte) ConfigurationHandler.timeout;

                Integer tries = this.syncBlacklist.get(realPos);
                if (tries == null) {
                    tries = 0;
                } else if (tries >= 10) {
                    return false;
                }

                Reference.logger.trace("Trying to sync block at {} {}", realPos, tries);
                final boolean success = handler.execute(player, this.schematic, pos, world, realPos);
                if (success) {
                    this.syncBlacklist.put(realPos, tries + 1);
                }

                return success;
            }

            return false;
        }

        if (ConfigurationHandler.destroyBlocks && !world.isAirBlock(realPos) && this.minecraft.playerController.isInCreativeMode()) {
            this.minecraft.playerController.clickBlock(realPos, EnumFacing.DOWN);

            this.timeout[x][y][z] = (byte) ConfigurationHandler.timeout;

            return !ConfigurationHandler.destroyInstantly;
        }

        if (this.schematic.isAirBlock(pos)) {
            return false;
        }

        if (!realBlock.isReplaceable(world, realPos)) {
            return false;
        }

        final ItemStack itemStack = BlockStateToItemStack.getItemStack(blockState, new RayTraceResult(player), this.schematic, pos, player);
        if (itemStack.isEmpty()) {
            Reference.logger.debug("{} is missing a mapping!", blockState);
            return false;
        }

        if (placeBlock(world, player, realPos, blockState, itemStack)) {
            this.timeout[x][y][z] = (byte) ConfigurationHandler.timeout;

            if (!ConfigurationHandler.placeInstantly) {
                return true;
            }
        }

        return false;
    }

    private boolean isSolid(final World world, final BlockPos pos, final EnumFacing side) {
        final BlockPos offset = pos.offset(side);

        final IBlockState blockState = world.getBlockState(offset);
        final Block block = blockState.getBlock();

        if (block == null) {
            return false;
        }

        if (block.isAir(blockState, world, offset)) {
            return false;
        }

        if (block instanceof BlockFluidBase) {
            return false;
        }

        if (block.isReplaceable(world, offset)) {
            return false;
        }

        return true;
    }

    private List<EnumFacing> getSolidSides(final World world, final BlockPos pos) {
        if (!ConfigurationHandler.placeAdjacent) {
            return Arrays.asList(EnumFacing.VALUES);
        }

        final List<EnumFacing> list = new ArrayList<EnumFacing>();

        for (final EnumFacing side : EnumFacing.VALUES) {
            if (isSolid(world, pos, side)) {
                list.add(side);
            }
        }

        return list;
    }

    private boolean placeBlock(final WorldClient world, final EntityPlayerSP player, final BlockPos pos, final IBlockState blockState, final ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBucket) {
            return false;
        }

        float x = getXvalue(pos, blockState);

        final PlacementData data = PlacementRegistry.INSTANCE.getPlacementData(blockState, itemStack);
        if (x == 0 && data != null && !data.isValidPlayerFacing(blockState, player, pos, world)) {
            return false;
        }

        final List<EnumFacing> solidSides = getSolidSides(world, pos);

        if (solidSides.size() == 0) {
            return false;
        }

        final EnumFacing direction;
        float offsetX;
        final float offsetY;
        final float offsetZ;
        final int extraClicks;

        if (data != null) {
            final List<EnumFacing> validDirections = data.getValidBlockFacings(solidSides, blockState);
            if (validDirections.size() == 0) {
                return false;
            }

            direction = validDirections.get(0);
            offsetX = data.getOffsetX(blockState);
            offsetY = data.getOffsetY(blockState);
            offsetZ = data.getOffsetZ(blockState);
            extraClicks = data.getExtraClicks(blockState);
        } else {
            direction = solidSides.get(0);
            offsetX = 0.5f;
            offsetY = 0.5f;
            offsetZ = 0.5f;
            extraClicks = 0;
        }

        if (!swapToItem(player.inventory, itemStack)) {
            return false;
        }

        return placeBlock(world, player, pos, direction, offsetX, offsetY, offsetZ, extraClicks, x);
    }

    private float getXvalue(BlockPos pos, IBlockState blockState) {

        float x = 0;

//        System.out.println("testing");
        if (toggleAccuratePlacement) {
            EnumFacing facing = null;
            if (checkBlockRotationDirectional(blockState)) {
                facing = blockState.getValue(BlockDirectional.FACING);
                x = pos.getX() + facing.getIndex() + 2;
            } else if (checkBlockRotationHorizontal(blockState)) {
                facing = blockState.getValue(BlockHorizontal.FACING);
                x = pos.getX() + facing.getIndex() + 2;
                if (blockState.getBlock() == Blocks.UNPOWERED_COMPARATOR) {
                    if ((BlockRedstoneComparator.Mode) blockState.getValue(BlockRedstoneComparator.MODE) == BlockRedstoneComparator.Mode.SUBTRACT) {
                        x = x + 10;
                    }
                } else if (blockState.getBlock() == Blocks.UNPOWERED_REPEATER) {
                    int value = (int) blockState.getValue(BlockRedstoneRepeater.DELAY);
                    x = x + ((value - 1) * 10);
                }
            } else if (checkBlockRotationStairTrap(blockState)) {
                facing = blockState.getValue(BlockHorizontal.FACING);
                boolean topHalf = false;
                if (blockState.getBlock() == Blocks.TRAPDOOR || blockState.getBlock() == Blocks.IRON_TRAPDOOR) {
                    topHalf = blockState.getValue(BlockTrapDoor.HALF) == BlockTrapDoor.DoorHalf.TOP;
                } else {
                    topHalf = blockState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;
                }
                x = pos.getX() + facing.getIndex() + 2;
                if(topHalf){
                    x = x + 10;
                }
//                System.out.println("facing " + facing);
            }
//            System.out.println("type " + blockState.getBlock() + " " + x + " " + (x - pos.getX()));
        }

        return x;
    }

    private boolean checkBlockRotationDirectional(IBlockState blockState) {
        for (Block b : checkListDirectional) {
            if (b == blockState.getBlock()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkBlockRotationHorizontal(IBlockState blockState) {
        for (Block b : checkListHorizontal) {
            if (b == blockState.getBlock()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkBlockRotationStairTrap(IBlockState blockState) {
        for (Block b : checkListStairTrap) {
            if (b == blockState.getBlock()) {
                return true;
            }
        }
        return false;
    }

    private boolean placeBlock(final WorldClient world, final EntityPlayerSP player, final BlockPos pos, final EnumFacing direction, final float offsetX, final float offsetY, final float offsetZ, final int extraClicks, float x) {
        final EnumHand hand = EnumHand.MAIN_HAND;
        final ItemStack itemStack = player.getHeldItem(hand);
        boolean success = false;

        if (!this.minecraft.playerController.isInCreativeMode() && !itemStack.isEmpty() && itemStack.getCount() <= extraClicks) {
            return false;
        }

        final BlockPos offset = pos.offset(direction);
        final EnumFacing side = direction.getOpposite();
        final Vec3d hitVec;

        if (x != 0) {
            hitVec = new Vec3d(x, offset.getY() + offsetY, offset.getZ() + offsetZ);
        } else {
            hitVec = new Vec3d(offset.getX() + offsetX, offset.getY() + offsetY, offset.getZ() + offsetZ);
        }

        success = placeBlock(world, player, itemStack, offset, side, hitVec, hand);
        for (int i = 0; success && i < extraClicks; i++) {
            success = placeBlock(world, player, itemStack, offset, side, hitVec, hand);
        }

        if (itemStack.getCount() == 0 && success) {
            player.inventory.mainInventory.set(player.inventory.currentItem, ItemStack.EMPTY);
        }

        return success;
    }

    private boolean placeBlock(final WorldClient world, final EntityPlayerSP player, final ItemStack itemStack, final BlockPos pos, final EnumFacing side, final Vec3d hitVec, final EnumHand hand) {
        // FIXME: where did this event go?
        /*
        if (ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, world, pos, side, hitVec).isCanceled()) {
            return false;
        }
        */

        // FIXME: when an adjacent block is not required the blocks should be placed 1 block away from the actual position (because air is replaceable)
        final BlockPos actualPos = ConfigurationHandler.placeAdjacent ? pos : pos.offset(side);
        final EnumActionResult result = this.minecraft.playerController.processRightClickBlock(player, world, actualPos, side, hitVec, hand);
        if ((result != EnumActionResult.SUCCESS)) {
            return false;
        }

        player.swingArm(hand);
        return true;
    }

    private void syncSneaking(final EntityPlayerSP player, final boolean isSneaking) {
        player.setSneaking(isSneaking);
        player.connection.sendPacket(new CPacketEntityAction(player, isSneaking ? CPacketEntityAction.Action.START_SNEAKING : CPacketEntityAction.Action.STOP_SNEAKING));
    }

    private boolean swapToItem(final InventoryPlayer inventory, final ItemStack itemStack) {
        return swapToItem(inventory, itemStack, true);
    }

    private boolean swapToItem(final InventoryPlayer inventory, final ItemStack itemStack, final boolean swapSlots) {
        final int slot = getInventorySlotWithItem(inventory, itemStack);

        if (this.minecraft.playerController.isInCreativeMode() && (slot < Constants.Inventory.InventoryOffset.HOTBAR || slot >= Constants.Inventory.InventoryOffset.HOTBAR + Constants.Inventory.Size.HOTBAR) && ConfigurationHandler.swapSlotsQueue.size() > 0) {
            inventory.currentItem = getNextSlot();
            inventory.setInventorySlotContents(inventory.currentItem, itemStack.copy());
            this.minecraft.playerController.sendSlotPacket(inventory.getStackInSlot(inventory.currentItem), Constants.Inventory.SlotOffset.HOTBAR + inventory.currentItem);
            return true;
        }

        if (slot >= Constants.Inventory.InventoryOffset.HOTBAR && slot < Constants.Inventory.InventoryOffset.HOTBAR + Constants.Inventory.Size.HOTBAR) {
            inventory.currentItem = slot;
            return true;
        } else if (swapSlots && slot >= Constants.Inventory.InventoryOffset.INVENTORY && slot < Constants.Inventory.InventoryOffset.INVENTORY + Constants.Inventory.Size.INVENTORY) {
            if (swapSlots(inventory, slot)) {
                return swapToItem(inventory, itemStack, false);
            }
        }

        return false;
    }

    private int getInventorySlotWithItem(final InventoryPlayer inventory, final ItemStack itemStack) {
        for (int i = 0; i < inventory.mainInventory.size(); i++) {
            if (inventory.mainInventory.get(i).isItemEqual(itemStack)) {
                return i;
            }
        }
        return -1;
    }

    private boolean swapSlots(final InventoryPlayer inventory, final int from) {
        if (ConfigurationHandler.swapSlotsQueue.size() > 0) {
            final int slot = getNextSlot();

            swapSlots(from, slot);
            return true;
        }

        return false;
    }

    private int getNextSlot() {
        final int slot = ConfigurationHandler.swapSlotsQueue.poll() % Constants.Inventory.Size.HOTBAR;
        ConfigurationHandler.swapSlotsQueue.offer(slot);
        return slot;
    }

    private boolean swapSlots(final int from, final int to) {
        return this.minecraft.playerController.windowClick(this.minecraft.player.inventoryContainer.windowId, from, to, ClickType.SWAP, this.minecraft.player) == ItemStack.EMPTY;
    }

    public boolean placeThisBlock(WorldClient world, EntityPlayerSP player, boolean timeout, BlockPos blockPos) {
        final int slot = player.inventory.currentItem;
        final boolean isSneaking = player.isSneaking();

        final IBlockState realBlockState = world.getBlockState(blockPos);
        final Block realBlock = realBlockState.getBlock();
        if (!realBlock.isReplaceable(world, blockPos)) {
            return false;
        }
        
        if (placeBlock(world, player, blockPos, timeout)) {
            return syncSlotAndSneaking(player, slot, isSneaking, true);
        }
        
        return false;
    }
    
    public void blockPlacer(WorldClient world, EntityPlayerSP player, boolean timeout){
        InputHandler.INSTANCE.placeBlock(world, ClientProxy.objectMouseOver, timeout, this);
    }
}
