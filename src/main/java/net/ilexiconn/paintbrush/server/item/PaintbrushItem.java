package net.ilexiconn.paintbrush.server.item;

import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
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
        setCreativeTab(CreativeTabs.tabTools);
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
        if (!world.isRemote) {
            EnumChatFormatting color = EnumChatFormatting.values()[getColorFromDamage(stack)];

            int size = getSizeFromDamage(stack);

            for (int ring = 0; ring < size; ring++) {
                for (int i = 0; i < 360; ++i) {
                    double rad = Math.toRadians((double) i);
                    int pX = (int) (-Math.sin(rad) * ring);
                    int pY = (int) (Math.cos(rad) * ring);
                    addPaint(world, side, hitX, hitY, hitZ, pos.getX(), pos.getY(), pos.getZ(), pX, pY, color);
                }
            }
        }

        return false;
    }

    private boolean addPaint(World world, EnumFacing facing, float hitX, float hitY, float hitZ, int blockX, int blockY, int blockZ, int paintX, int paintY, EnumChatFormatting color) {
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

        PaintedBlockEntity paintedBlock = getPaintEntity(world, new BlockPos(blockX, blockY, blockZ));

        if (paintedBlock.canStay()) {
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
            return paintedBlock.addPaint(paint);
        }
        return false;
    }

    private PaintedBlockEntity getPaintEntity(World world, BlockPos pos) {
        PaintedBlockEntity paintedBlock = null;
        for (Object entity : world.loadedEntityList) {
            if (entity instanceof PaintedBlockEntity) {
                PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;
                if (paintedBlockEntity.blockPos.equals(pos)) {
                    paintedBlock = paintedBlockEntity;
                    break;
                }
            }
        }
        if (paintedBlock == null) {
            paintedBlock = new PaintedBlockEntity(world);
            paintedBlock.blockPos = pos;
            paintedBlock.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);
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
            paintPos += 16;
            blockPos--;
        }

        return new int[]{blockPos, paintPos};
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
}
