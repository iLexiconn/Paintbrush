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

    public static int getColorFromDamage(ItemStack stack) {
        return stack.getItemDamage() & 0b1111;
    }

    public static int getInkFromDamage(ItemStack stack) {
        return (stack.getItemDamage() >>> 4) & 0b111111;
    }

    public static int getSizeFromDamage(ItemStack stack) {
        return (stack.getItemDamage() >>> 10) & 0b111;
    }

    public static boolean isStackInfinite(ItemStack stack) {
        return ((stack.getItemDamage() >>> 13) & 0b1) == 1;
    }

    public static int getDamage(int color, int ink, int size, boolean infinite) {
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
            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];

            EnumFacing facing = EnumFacing.values()[face];

            int size = getSizeFromDamage(stack);

            for (int ring = 0; ring < size; ring++) {
                for (int i = 0; i < 360; ++i) {
                    double rad = Math.toRadians((double) i);
                    int pX = (int) (-Math.sin(rad) * ring);
                    int pY = (int) (Math.cos(rad) * ring);

                    addPaint(world, facing, hitX, hitY, hitZ, x, y, z, pX, pY, color);
                }
            }
        }

        return false;
    }

    private void addPaint(World world, EnumFacing facing, float hitX, float hitY, float hitZ, int blockX, int blockY, int blockZ, int paintX, int paintY, EnumChatFormatting color) {
        int blockPaintPosX = (int) (hitX * 16);
        int blockPaintPosY = (int) (hitY * 16);
        int blockPaintPosZ = (int) (hitZ * 16);

        if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
            blockPaintPosX += paintX;
            blockPaintPosZ += paintY;
        } else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
            blockPaintPosX += paintX;
            blockPaintPosY += paintY;
        } else if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
            blockPaintPosZ += paintX;
            blockPaintPosY += paintY;
        }

        int[] offsetsX = offsetPos(blockPaintPosX, blockX);
        int[] offsetsY = offsetPos(blockPaintPosY, blockY);
        int[] offsetsZ = offsetPos(blockPaintPosZ, blockZ);

        if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
            blockX = offsetsX[0];
            blockZ = offsetsZ[0];
            blockPaintPosX = offsetsX[1];
            blockPaintPosZ = offsetsZ[1];
        } else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
            blockX = offsetsX[0];
            blockY = offsetsY[0];
            blockPaintPosX = offsetsX[1];
            blockPaintPosY = offsetsY[1];
        } else if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
            blockZ = offsetsZ[0];
            blockY = offsetsY[0];
            blockPaintPosY = offsetsY[1];
            blockPaintPosZ = offsetsZ[1];
        }

        PaintedBlockEntity paintedBlock = getPaintEntity(world, blockX, blockY, blockZ);

        int offsetX = 0;
        int offsetY = 0;

        if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
            offsetX = blockPaintPosX;
            offsetY = blockPaintPosZ;
        } else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
            offsetX = blockPaintPosX;
            offsetY = blockPaintPosY;
        } else if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
            offsetX = blockPaintPosZ;
            offsetY = blockPaintPosY;
        }

        Paint paint = new Paint(facing, offsetX, offsetY, color);
        paintedBlock.addPaint(paint);
    }

    private PaintedBlockEntity getPaintEntity(World world, int x, int y, int z) {
        PaintedBlockEntity paintedBlock = null;
        for (Object entity : world.loadedEntityList) {
            if (entity instanceof PaintedBlockEntity) {
                PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;
                if (paintedBlockEntity.blockX == x && paintedBlockEntity.blockY == y && paintedBlockEntity.blockZ == z) {
                    paintedBlock = paintedBlockEntity;
                    break;
                }
            }
        }
        if (paintedBlock == null) {
            paintedBlock = new PaintedBlockEntity(world);
            paintedBlock.blockX = x;
            paintedBlock.blockY = y;
            paintedBlock.blockZ = z;
            paintedBlock.setPositionAndRotation(x + 0.5F, y, z + 0.5F, 0, 0);
            world.spawnEntityInWorld(paintedBlock);
        }

        return paintedBlock;
    }

    private int[] offsetPos(int paintPos, int blockPos) {
        if (paintPos > 15) {
            paintPos = paintPos % 16;
            blockPos++;
        } else if (paintPos < 0) {
            paintPos = paintPos % 16;
            blockPos--;
        }

        return new int[] { blockPos, paintPos };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips) {
        EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.color") + ": " + (getInkFromDamage(stack) != getMaxDamage() ? color + StatCollector.translateToLocal("color." + color.getFriendlyName() + ".name") : "-"));
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.size") + ": " + getSizeFromDamage(stack));
        //info.add(StatCollector.translateToLocal("tooltip.paintbrush.ink") + ": " + (isStackInfinite(stack) ? "∞" : (getMaxDamage() - getInkFromDamage(stack))));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        //items.add(new ItemStack(item, 1, getDamage(0, 0, 1, false)));
        for (int i = 0; i < 16; i++) {
            items.add(new ItemStack(item, 1, getDamage(i, 0, 1, true)));
        }
    }

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
