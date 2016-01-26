package net.ilexiconn.paintbrush.server;

import com.google.common.collect.Lists;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.MessageUpdateData;
import net.ilexiconn.paintbrush.server.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class PaintbrushDataServer extends WorldSavedData {
    private static PaintbrushDataServer instance;

    public List<PaintedBlock> paintedBlockList = Lists.newArrayList();

    public PaintbrushDataServer() {
        super("paintbrush");
    }

    public PaintbrushDataServer(String identifier) {
        super(identifier);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList paint = compound.getTagList("paint", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < paint.tagCount(); i++) {
            NBTTagCompound paintTag = paint.getCompoundTagAt(i);
            paintedBlockList.add(new PaintedBlock().readFromNBT(paintTag));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList paint = new NBTTagList();

        for (PaintedBlock paintedBlock : paintedBlockList) {
            NBTTagCompound paintTag = new NBTTagCompound();
            paintedBlock.writeToNBT(paintTag);
            paint.appendTag(paintTag);
        }

        compound.setTag("paint", paint);
    }

    public PaintedBlock getPaintedBlock(BlockPos pos) {
        for (PaintedBlock paintedBlock : paintedBlockList) {
            if (pos.equals(paintedBlock.pos)) {
                return paintedBlock;
            }
        }

        return null;
    }

    public void addPaintedBlock(PaintedBlock paintedBlock) {
        paintedBlockList.add(paintedBlock);
        Paintbrush.networkWrapper.sendToAll(new MessageUpdateData(Utils.BLOCK, paintedBlock, true));
        markDirty();
    }

    public void addPaintedFace(PaintedBlock paintedBlock, PaintedFace paintedFace) {
        paintedBlock.paintedFaceList.add(paintedFace);
        Paintbrush.networkWrapper.sendToAll(new MessageUpdateData(Utils.FACE, paintedFace, true));
        markDirty();
    }

    public void addPaint(PaintedFace paintedFace, Paint paint) {
        if (!hasPaint(paintedFace, paint.x, paint.y)) {
            paintedFace.paintList.add(paint);
            Paintbrush.networkWrapper.sendToAll(new MessageUpdateData(Utils.PAINT, paint, true));
            markDirty();
        }
    }

    public boolean hasPaint(PaintedFace paintedFace, int x, int y) {
        for (Paint paint : paintedFace.paintList) {
            if (paint.x == x && paint.y == y) {
                return true;
            }
        }
        return false;
    }

    public void removePaintedBlock(PaintedBlock paintedBlock) {
        if (paintedBlockList.contains(paintedBlock)) {
            paintedBlockList.remove(paintedBlock);
            //Paintbrush.networkWrapper.sendToAll(new MessageUpdateData(Utils.BLOCK, paintedBlock, true));
            markDirty();
        }
    }

    public void removePaintedFace(PaintedFace paintedFace) {
        for (PaintedBlock paintedBlock : paintedBlockList) {
            if (paintedBlock.paintedFaceList.contains(paintedFace)) {
                paintedBlock.paintedFaceList.remove(paintedFace);
                //Paintbrush.networkWrapper.sendToAll(new MessageUpdateData(Utils.FACE, paintedFace, true));
                markDirty();
            }
        }
    }

    public static PaintbrushDataServer get(World world) {
        if (instance == null) {
            if (!world.isRemote) {
                MapStorage storage = world.perWorldStorage;
                PaintbrushDataServer result = (PaintbrushDataServer) storage.loadData(PaintbrushDataServer.class, "paintbrush");
                if (result == null) {
                    result = new PaintbrushDataServer("paintbrush");
                    result.markDirty();
                    storage.setData("paintbrush", result);
                }
                instance = result;
                return result;
            }
            return null;
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }
}
