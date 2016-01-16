package net.ilexiconn.paintbrush.server.world;

import com.google.common.collect.Lists;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.MessageUpdatePaint;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants;

import java.util.Iterator;
import java.util.List;

public class PaintbrushData extends WorldSavedData {
    private List<Paint> paintList = Lists.newArrayList();
    public static PaintbrushData instance;

    public PaintbrushData() {
        super("paintbrush");
    }

    public PaintbrushData(String identifier) {
        super(identifier);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList paint = compound.getTagList("Paint", Constants.NBT.TAG_LIST);

        for (int i = 0; i < paint.tagCount(); i++) {
            NBTTagCompound paintTag = paint.getCompoundTagAt(i);
            paintList.add(Paint.readFromNBT(paintTag));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList pixels = new NBTTagList();

        for (Paint paint : paintList) {
            NBTTagCompound paintTag = new NBTTagCompound();
            paint.writeToNBT(paintTag);
            pixels.appendTag(paintTag);
        }

        compound.setTag("Paint", pixels);
    }

    public Paint getPaint(int x, int y, int z, int face) {
        for (Paint paint : paintList) {
            BlockPos pos = paint.getPos();
            if (x == pos.getX() && y == pos.getY() && z == pos.getZ() && face == paint.getFacing().ordinal()) {
                return paint;
            }
        }
        return null;
    }

    public void addPaint(Paint paint) {
        Iterator<Paint> iterator = paintList.iterator();

        while (iterator.hasNext()) {
            Paint p = iterator.next();
            if (p.getPos().equals(paint.getPos()) && p.getFacing() == paint.getFacing() && p.getX() == paint.getX() && p.getY() == paint.getY()) {
                iterator.remove();
            }
        }

        paintList.add(paint);
        Paintbrush.networkWrapper.sendToAll(new MessageUpdatePaint(paint));
        markDirty();
    }

    public void removePaint(Paint paint) {
        paintList.remove(paint);
    }

    public Paint[] getPaint() {
        return paintList.toArray(new Paint[paintList.size()]);
    }

    public static PaintbrushData get(World world) {
        if (PaintbrushData.instance == null) {
            if (!world.isRemote) {
                MapStorage storage = world.perWorldStorage;

                PaintbrushData result = (PaintbrushData) storage.loadData(PaintbrushData.class, "paintbrush");
                if (result == null) {
                    result = new PaintbrushData("paintbrush");
                    result.markDirty();
                    storage.setData("paintbrush", result);
                }

                PaintbrushData.instance = result;

                return result;
            }

            return null;
        }

        return PaintbrushData.instance;
    }
}
