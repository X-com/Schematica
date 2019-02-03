package com.github.lunatrius.schematica.util;

import mixin.IMixinMinecraft;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class PickBlock {

    /**
     * Called when a player uses 'pick block', calls new Entity and Block hooks.
     */
    public static boolean onPickBlock(RayTraceResult target, EntityPlayer player, World world)
    {
        ItemStack result;
        boolean isCreative = player.capabilities.isCreativeMode;
        TileEntity te = null;

        if (target.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            IBlockState state = world.getBlockState(target.getBlockPos());

            if (state.getBlock().getMaterial(state) == Material.AIR)
            {
                return false;
            }

            if (isCreative && GuiScreen.isCtrlKeyDown() && state.getBlock().hasTileEntity())
                te = world.getTileEntity(target.getBlockPos());

            result = state.getBlock().getItem(world, target.getBlockPos(), state);
        }
        else
        {
            if (target.typeOfHit != RayTraceResult.Type.ENTITY || target.entityHit == null || !isCreative)
            {
                return false;
            }

            result = getPickedResult(target.entityHit, target);
        }

        if (result.isEmpty())
        {
            return false;
        }

        if (te != null)
        {
            ((IMixinMinecraft)Minecraft.getMinecraft()).callStoreTEInStack(result, te);
        }

        if (isCreative)
        {
            player.inventory.setPickedItemStack(result);
            Minecraft.getMinecraft().playerController.sendSlotPacket(player.getHeldItem(EnumHand.MAIN_HAND), 36 + player.inventory.currentItem);
            return true;
        }
        int slot = player.inventory.getSlotFor(result);
        if (slot != -1)
        {
            if (InventoryPlayer.isHotbar(slot))
                player.inventory.currentItem = slot;
            else
                Minecraft.getMinecraft().playerController.pickItem(slot);
            return true;
        }
        return false;
    }

    /**
     * Called when a user uses the creative pick block button on this entity.
     *
    * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, empty ItemStack if nothing should be added.
     */
    public static ItemStack getPickedResult(Entity hit, RayTraceResult target)
    {
        if (hit instanceof net.minecraft.entity.item.EntityPainting)
        {
            return new ItemStack(net.minecraft.init.Items.PAINTING);
        }
        else if (hit instanceof EntityLeashKnot)
        {
            return new ItemStack(net.minecraft.init.Items.LEAD);
        }
        else if (hit instanceof EntityItemFrame)
        {
//            ItemStack held = ((EntityItemFrame)hit).func_82335_i();
//            if (held.getHasSubtypes())
//            {
//                return new ItemStack(Items.ITEM_FRAME);
//            }
//            else
//            {
//                return held.copy();
//            }
            return new ItemStack(Items.ITEM_FRAME);
        }
        else if (hit instanceof net.minecraft.entity.item.EntityMinecart)
        {
//            return ((net.minecraft.entity.item.EntityMinecart)hit).getCartItem();
            return new ItemStack(Items.MINECART);
        }
        else if (hit instanceof net.minecraft.entity.item.EntityBoat)
        {
            return new ItemStack(Items.BOAT);
        }
        else if (hit instanceof net.minecraft.entity.item.EntityArmorStand)
        {
            return new ItemStack(Items.ARMOR_STAND);
        }
        else if (hit instanceof net.minecraft.entity.item.EntityEnderCrystal)
        {
            return new ItemStack(Items.END_CRYSTAL);
        }
        else
        {
            ResourceLocation name = EntityList.getKey(hit);
            if (name != null && EntityList.REGISTRY.containsKey(name))
            {
                ItemStack stack = new ItemStack(Items.SPAWN_EGG);
                net.minecraft.item.ItemMonsterPlacer.applyEntityIdToItemStack(stack, name);
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
