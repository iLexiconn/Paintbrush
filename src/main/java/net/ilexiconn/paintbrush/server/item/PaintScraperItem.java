package net.ilexiconn.paintbrush.server.item;

import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.api.PaintbrushAPI;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PaintScraperItem extends Item {
    public PaintScraperItem() {
        setUnlocalizedName("paint_scraper");
        setCreativeTab(Paintbrush.creativeTab);
        setMaxStackSize(1);
        setMaxDamage(64);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        PaintbrushAPI.removePaint(world, pos, hitX, hitY, hitZ, side, stack.getItemDamage());
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean advancedTooltips) {
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.size") + ": " + stack.getItemDamage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        items.add(new ItemStack(item, 1, 1));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
