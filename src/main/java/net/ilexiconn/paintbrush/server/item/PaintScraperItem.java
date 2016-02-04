package net.ilexiconn.paintbrush.server.item;

import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
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
        setCreativeTab(CreativeTabs.tabTools);
        setMaxStackSize(1);
        setMaxDamage(64);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            for (int ring = 0; ring < stack.getItemDamage(); ring++) {
                for (int i = 0; i < 360; ++i) {
                    double rad = Math.toRadians((double) i);
                    int pX = (int) (-Math.sin(rad) * ring);
                    int pY = (int) (Math.cos(rad) * ring);

                    removePaint(world, side, hitX, hitY, hitZ, pos.getX(), pos.getY(), pos.getZ(), pX, pY);
                }
            }
        }

        return false;
    }

    private void removePaint(World world, EnumFacing facing, float hitX, float hitY, float hitZ, int blockX, int blockY, int blockZ, int paintX, int paintY) {
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
        if (paintedBlock == null) {
            return;
        }

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

            paintedBlock.removePaint(offsetX, offsetY, facing);
        }
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

        return new int[]{blockPos, paintPos};
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
