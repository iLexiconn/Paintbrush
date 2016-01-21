package net.ilexiconn.paintbrush.server.world;

import com.google.common.collect.Lists;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.MessageUpdatePaint;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.ilexiconn.paintbrush.server.util.PaintedBlock;
import net.ilexiconn.paintbrush.server.util.PaintedFace;
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
            paintedBlocks.add(PaintedBlock.readFromNBT(paintTag));
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
        for (PaintedBlock paintedBlock : paintedBlocks) {
            if (paintedBlock.pos == pos) {
                for (PaintedFace paintedFace : paintedBlock.paintedFaces) {
                    if (paintedFace.facing == facing) {
                        for (Paint paint : paintedFace.paint) {
                            if (paint.x == x && paint.y == y) {
                                return paint;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void addPaint(PaintedBlock paintedBlock) {
        paintedBlocks.add(paintedBlock);
        Paintbrush.networkWrapper.sendToAll(new MessageUpdatePaint(paintedBlock));
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
