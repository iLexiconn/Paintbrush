package net.ilexiconn.paintbrush.server.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.api.PaintbrushAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class PaintbrushItem extends Item {
    @SideOnly(Side.CLIENT)
    private IIcon colorOverlay;

    public PaintbrushItem() {
        setUnlocalizedName("paintbrush");
        setCreativeTab(Paintbrush.creativeTab);
        setTextureName("paintbrush:paintbrush");
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

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);
        this.colorOverlay = iconRegister.registerIcon("paintbrush:paintbrush_overlay");
    }

    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 0 ? itemIcon : colorOverlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass != 0) {
            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
            return getColorCode(color.getFormattingCode(), Minecraft.getMinecraft().fontRenderer);
        } else {
            return 0xFFFFFF;
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
        EnumFacing facing = EnumFacing.values()[face];
        int size = getSizeFromDamage(stack);
        PaintbrushAPI.addPaint(world, x, y, z, hitX, hitY, hitZ, facing, color, size);
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

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
