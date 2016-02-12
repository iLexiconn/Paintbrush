package net.ilexiconn.paintbrush.server.api;

import com.google.common.collect.Lists;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.block.Block;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

public class PaintbrushAPI {
    private static List<Block> ignoredBlockList = Lists.newArrayList();
    private static List<Class<?>> ignoredBlockTypeList = Lists.newArrayList();

    public static void registerIgnoredBlock(Block block) {
        ignoredBlockList.add(block);
    }

    public static void registerIgnoredBlockType(Class<?> type) {
        ignoredBlockTypeList.add(type);
    }

    public static boolean isBlockIgnored(Block block) {
        if (!ignoredBlockList.contains(block)) {
            for (Class<?> type : ignoredBlockTypeList) {
                if (type.isAssignableFrom(block.getClass())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public static int addPaint(World world, int x, int y, int z, float hitX, float hitY, float hitZ, EnumFacing facing, EnumChatFormatting color, int radius) {
        int j = 0;
        if (!world.isRemote) {
            for (int ring = 0; ring < radius; ring++) {
                for (int i = 0; i < 360; ++i) {
                    double rad = Math.toRadians((double) i);
                    int pX = (int) (-Math.sin(rad) * ring);
                    int pY = (int) (Math.cos(rad) * ring);
                    if (addPaint(world, facing, hitX, hitY, hitZ, x, y, z, pX, pY, color)) {
                        j++;
                    }
                }
            }
        }
        return j;
    }

    public static int removePaint(World world, int x, int y, int z, float hitX, float hitY, float hitZ, EnumFacing facing, int radius) {
        int j = 0;
        if (!world.isRemote) {
            for (int ring = 0; ring < radius; ring++) {
                for (int i = 0; i < 360; ++i) {
                    double rad = Math.toRadians((double) i);
                    int pX = (int) (-Math.sin(rad) * ring);
                    int pY = (int) (Math.cos(rad) * ring);
                    if (removePaint(world, facing, hitX, hitY, hitZ, x, y, z, pX, pY)) {
                        j++;
                    }
                }
            }
        }
        return j;
    }

    private static boolean addPaint(World world, EnumFacing facing, float hitX, float hitY, float hitZ, int blockX, int blockY, int blockZ, int paintX, int paintY, EnumChatFormatting color) {
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

        PaintedBlockEntity paintedBlock = getPaintEntity(world, blockX, blockY, blockZ, true);

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

    private static boolean removePaint(World world, EnumFacing facing, float hitX, float hitY, float hitZ, int blockX, int blockY, int blockZ, int paintX, int paintY) {
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

        PaintedBlockEntity paintedBlock = getPaintEntity(world, blockX, blockY, blockZ, false);

        if (paintedBlock != null) {
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

            return paintedBlock.removePaint(offsetX, offsetY, facing);
        }
        return false;
    }

    private static PaintedBlockEntity getPaintEntity(World world, int x, int y, int z, boolean createNew) {
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
        if (paintedBlock == null && createNew) {
            paintedBlock = new PaintedBlockEntity(world);
            paintedBlock.blockX = x;
            paintedBlock.blockY = y;
            paintedBlock.blockZ = z;
            paintedBlock.setPositionAndRotation(x + 0.5F, y, z + 0.5F, 0, 0);
            world.spawnEntityInWorld(paintedBlock);
        }

        return paintedBlock;
    }

    private static int[] offsetPos(int paintPos, int blockPos) {
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
}
