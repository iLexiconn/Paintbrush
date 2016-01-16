package net.ilexiconn.paintbrush.server.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.ilexiconn.paintbrush.server.world.PaintbrushData;
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
        setCreativeTab(CreativeTabs.tabTools);
        setTextureName("paintbrush:paintbrush");
    }

    public int getColorFromDamage(ItemStack stack) {
        return stack.getItemDamage() & 0b1111;
    }

    @SuppressWarnings("deprecation")
    public int getInkFromDamage(ItemStack stack) {
        return (stack.getItem().getDisplayDamage(stack) >>> 4) & 0b111111;
    }

    public int getSizeFromDamage(ItemStack stack) {
        return (stack.getItemDamage() >>> 10) & 0b111;
    }

    public int getDamage(int color, int ink, int size) {
        return (color & 0b1111) | (ink << 4) | (size << 10);
    }

    public void setInk(ItemStack stack, int ink) {
        stack.setItemDamage(getDamage(getColorFromDamage(stack), ink, getSizeFromDamage(stack)));
    }

    public void setSize(ItemStack stack, int size) {
        stack.setItemDamage(getDamage(getColorFromDamage(stack), getInkFromDamage(stack), size));
    }

    @Override
    public int getMaxDamage() {
        return 64;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return getInkFromDamage(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
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
        return pass == 0 || ((damage >>> 4) & 0b111111) == 0 ? itemIcon : colorOverlay;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass != 0 && getInkFromDamage(stack) != 0) {
            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
            return getColorCode(color.getFormattingCode(), Minecraft.getMinecraft().fontRenderer);
        } else {
            return 0xFFFFFF;
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            PaintbrushData data = PaintbrushData.get(world);
            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
            Paint paint = new Paint(color, (int) Math.floor(hitX / 0.0625f), (int) Math.floor(hitY / 0.0625f), EnumFacing.values()[face], new BlockPos(x, y, z));
            data.addPaint(paint);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips) {
        EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.color") + ": " + (getInkFromDamage(stack) != 0 ? color + StatCollector.translateToLocal("color." + color.getFriendlyName() + ".name") : "-"));
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.size") + ": " + getSizeFromDamage(stack));
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.ink") + ": " + getInkFromDamage(stack));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        for (int i = 0; i < 16; i++) {
            items.add(new ItemStack(item, 1, getDamage(i, itemRand.nextInt(65), itemRand.nextInt(5) + 1)));
        }
    }

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
