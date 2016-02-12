package net.ilexiconn.paintbrush.server.item;

import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.api.PaintbrushAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PaintbrushItem extends Item {
    public PaintbrushItem() {
        setUnlocalizedName("paintbrush");
        setCreativeTab(Paintbrush.creativeTab);
        setMaxStackSize(1);
    }

    public static int getColorFromDamage(ItemStack stack) {
        return stack.getItemDamage() & 0B1111;
    }

    public static int getSizeFromDamage(ItemStack stack) {
        return (stack.getItemDamage() >>> 4) & 0B111;
    }

    public static int getDamage(int color, int size) {
        return (color & 0B1111) | (size << 4);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass != 0) {
            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
            return Minecraft.getMinecraft().fontRendererObj.getColorCode(color.formattingCode);
        } else {
            return 0xFFFFFF;
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
        int size = getSizeFromDamage(stack);
        PaintbrushAPI.addPaint(world, pos, hitX, hitY, hitZ, side, color, size);
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips) {
        EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.color") + ": " + color + StatCollector.translateToLocal("color." + color.getFriendlyName() + ".name"));
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.size") + ": " + getSizeFromDamage(stack));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        for (int i = 0; i < 16; i++) {
            items.add(new ItemStack(item, 1, getDamage(i, 1)));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
