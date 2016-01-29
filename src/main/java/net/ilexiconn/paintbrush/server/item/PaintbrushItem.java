package net.ilexiconn.paintbrush.server.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
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
            PaintedBlockEntity paintedBlock = null;
            List<PaintedBlockEntity> paintedBlockList = world.getEntitiesWithinAABB(PaintedBlockEntity.class, AxisAlignedBB.getBoundingBox(x - 1.0F, y - 1.0F, z - 1.0F, x + 1.0F, y + 1.0F, z + 1.0F).expand(2.0F, 2.0F, 2.0F));
            for (PaintedBlockEntity entity : paintedBlockList) {
                System.out.println("[" + (int) Math.floor(entity.posX) + ", " + x + "], [" + (int) entity.posY + ", " + y + "], [" + (int) entity.posY + ", " + y + "]");
                if ((int) entity.posX == x && (int) entity.posY == y && (int) entity.posZ == z) {
                    paintedBlock = entity;
                }
            }
            if (paintedBlock == null) {
                System.out.println("Entity is null, creating new one...");
                paintedBlock = new PaintedBlockEntity(world);
                paintedBlock.setPositionAndRotation(x + 0.5F, y, z + 0.5F, 0, 0);
                world.spawnEntityInWorld(paintedBlock);
            }

            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];

            int pixelX = (int) (hitX * 16);
            int pixelY = (int) (hitY * 16);
            int pixelZ = (int) (hitZ * 16);

            int offsetX = 0;
            int offsetY = 0;

            EnumFacing facing = EnumFacing.values()[face];

            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                offsetX = pixelX;
                offsetY = pixelZ;
            } else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                offsetX = pixelX;
                offsetY = pixelY;
            } else if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
                offsetX = pixelZ;
                offsetY = pixelY;
            }

            int size = getSizeFromDamage(stack);

            Paint paint = new Paint(facing, offsetX, offsetY, color);
            paintedBlock.addPaint(paint);
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
        items.add(new ItemStack(item, 1, getDamage(0, 0, 1, false)));
        for (int i = 0; i < 16; i++) {
            items.add(new ItemStack(item, 1, getDamage(i, 0, 1, true)));
        }
    }

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
