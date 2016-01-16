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

    //000000|0000

    public int getColorFromDamage(int damage) {
        return damage & 0b1111;
    }

    public int getInkFromDamage(int damage) {
        return damage >>> 4;
    }

    public int getDamageForColorAndInk(int color, int ink) {
        return (color & 0b1111) | (ink << 4);
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

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass != 0) {
            int damage = stack.getItemDamage();
            EnumChatFormatting color = EnumChatFormatting.values()[damage];
            return getColorCode(color.getFormattingCode(), Minecraft.getMinecraft().fontRenderer);
        } else {
            return 0xFFFFFF;
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            PaintbrushData data = PaintbrushData.get(world);
            int damage = stack.getItemDamage();
            EnumChatFormatting color = EnumChatFormatting.values()[damage];
            Paint paint = new Paint(color, itemRand.nextInt(15), itemRand.nextInt(15), EnumFacing.values()[face], new BlockPos(x, y, z));
            data.addPaint(paint);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips) {
        int damage = stack.getItemDamage();
        EnumChatFormatting color = EnumChatFormatting.values()[damage];
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.color") + ": " + color + StatCollector.translateToLocal("color." + color.getFriendlyName() + ".name"));
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.size") + ": 1");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        for (int dyeType = 0; dyeType < 16; dyeType++) {
            items.add(new ItemStack(item, 1, dyeType));
        }
    }

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
