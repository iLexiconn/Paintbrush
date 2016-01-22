package net.ilexiconn.paintbrush.server.world;

import com.google.common.collect.Lists;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.MessageUpdateData;
import net.ilexiconn.paintbrush.server.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class PaintbrushData extends WorldSavedData {
    private List<PaintedBlock> paintedBlocks = Lists.newArrayList();
    private static PaintbrushData instance;

    public PaintbrushData() {
        super("paintbrush");
    }

    public PaintbrushData(String identifier) {
        super(identifier);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList paint = compound.getTagList("paint", Constants.NBT.TAG_LIST);

        for (int i = 0; i < paint.tagCount(); i++) {
            NBTTagCompound paintTag = paint.getCompoundTagAt(i);
            paintedBlocks.add(new PaintedBlock().readFromNBT(paintTag));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList paint = new NBTTagList();

        for (PaintedBlock paintedBlock : paintedBlocks) {
            NBTTagCompound paintTag = new NBTTagCompound();
            paintedBlock.writeToNBT(paintTag);
            paint.appendTag(paintTag);
        }

        compound.setTag("paint", paint);
    }

    public Paint getPaint(BlockPos pos, EnumFacing facing, int x, int y) {
        PaintedBlock paintedBlock = getPaintedBlock(pos);

        if (paintedBlock != null) {
            PaintedFace paintedFace = paintedBlock.getPaintedFace(facing);

            if (paintedFace != null) {
                return paintedFace.getPaint(x, y);
            }
        }

        return null;
    }

    public PaintedBlock getPaintedBlock(BlockPos pos) {
        for (PaintedBlock paintedBlock : paintedBlocks) {
            if (pos.equals(paintedBlock.pos)) {
                return paintedBlock;
            }
        }

        return null;
    }

    public void addPaintedBlock(PaintedBlock paintedBlock) {
        paintedBlocks.add(paintedBlock);
        Paintbrush.networkWrapper.sendToAll(new MessageUpdateData(Utils.BLOCK, paintedBlock));
        markDirty();
    }

    public List<PaintedBlock> getPaintedBlocks() {
        return paintedBlocks;
    }

    public static PaintbrushData get(World world) {
        if (instance == null) {
            if (!world.isRemote) {
                MapStorage storage = world.perWorldStorage;
                PaintbrushData result = (PaintbrushData) storage.loadData(PaintbrushData.class, "paintbrush");
                if (result == null) {
                    result = new PaintbrushData("paintbrush");
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
