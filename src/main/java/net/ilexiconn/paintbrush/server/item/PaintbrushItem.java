package net.ilexiconn.paintbrush.server.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.PaintedBlock;
import net.ilexiconn.paintbrush.server.util.PaintedFace;
import net.ilexiconn.paintbrush.server.PaintbrushDataServer;
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
        setMaxDamage(64);
        setMaxStackSize(1);
    }

    public int getColorFromDamage(ItemStack stack) {
        return stack.getItemDamage() & 0b1111;
    }

    public int getInkFromDamage(ItemStack stack) {
        return (stack.getItemDamage() >>> 4) & 0b111111;
    }

    public int getSizeFromDamage(ItemStack stack) {
        return (stack.getItemDamage() >>> 10) & 0b111;
    }

    public boolean isStackInfinite(ItemStack stack) {
        return ((stack.getItemDamage() >>> 13) & 0b1) == 1;
    }

    public int getDamage(int color, int ink, int size, boolean infinite) {
        return (color & 0b1111) | (ink << 4) | (size << 10) | (infinite ? 1 << 13 : 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getDisplayDamage(ItemStack stack) {
        return getInkFromDamage(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return !isStackInfinite(stack);
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
        return pass == 0 || ((damage >>> 4) & 0b111111) == getMaxDamage() ? itemIcon : colorOverlay;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass != 0 && getInkFromDamage(stack) != getMaxDamage()) {
            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
            return getColorCode(color.getFormattingCode(), Minecraft.getMinecraft().fontRenderer);
        } else {
            return 0xFFFFFF;
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            PaintbrushDataServer data = PaintbrushDataServer.get(world);
            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];

            int resolution = 16;

            int pixelX = (int) (hitX * resolution);
            int pixelY = (int) (hitY * resolution);
            int pixelZ = (int) (hitZ * resolution);

            int drawX = 0;
            int drawY = 0;

            EnumFacing facing = EnumFacing.values()[face];

            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                drawX = pixelX;
                drawY = pixelZ;
            } else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                drawX = pixelX;
                drawY = pixelY;
            } else if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
                drawX = pixelZ;
                drawY = pixelY;
            }

            BlockPos pos = new BlockPos(x, y, z);
            PaintedBlock paintedBlock = data.getPaintedBlock(pos);

            if (paintedBlock == null) {
                paintedBlock = new PaintedBlock();
                paintedBlock.pos = pos;
                data.addPaintedBlock(paintedBlock);
            }

            PaintedFace paintedFace = paintedBlock.getPaintedFace(facing);

            if (paintedFace == null) {
                paintedFace = new PaintedFace();
                paintedFace.facing = facing;
                data.addPaintedFace(paintedBlock, paintedFace);
            }

            int size = getSizeFromDamage(stack);

            for (int ring = 0; ring < size; ring++) {
                for (int i = 0; i < 360; ++i) {
                    double rad = Math.toRadians((double) i);
                    int pX = (int) (-Math.sin(rad) * ring);
                    int pY = (int) (Math.cos(rad) * ring);
                    data.addPaint(paintedFace, drawX + pX, drawY + pY, color);
                }
            }
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips) {
        EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.color") + ": " + (getInkFromDamage(stack) != getMaxDamage() ? color + StatCollector.translateToLocal("color." + color.getFriendlyName() + ".name") : "-"));
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.size") + ": " + getSizeFromDamage(stack));
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.ink") + ": " + (isStackInfinite(stack) ? "∞" : (getMaxDamage() - getInkFromDamage(stack))));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        for (int i = 0; i < 16; i++) {
            items.add(new ItemStack(item, 1, getDamage(i, 0, 1, true)));
        }
    }

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
