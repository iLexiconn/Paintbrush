package net.ilexiconn.paintbrush.server.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.api.PaintbrushAPI;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class PaintScraperItem extends Item {
    public PaintScraperItem() {
        setUnlocalizedName("paint_scraper");
        setCreativeTab(Paintbrush.creativeTab);
        setTextureName("paintbrush:paint_scraper");
        setMaxStackSize(1);
        setMaxDamage(64);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        EnumFacing facing = EnumFacing.values()[face];
        PaintbrushAPI.removePaint(world, x, y, z, hitX, hitY, hitZ, facing, stack.getItemDamage());
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips) {
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.size") + ": " + stack.getItemDamage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        items.add(new ItemStack(item, 1, 1));
    }
}
