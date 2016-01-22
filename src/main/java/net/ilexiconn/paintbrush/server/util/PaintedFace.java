package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

public class PaintedFace implements Util<PaintedBlock> {
    public EnumFacing facing;
    public Paint[] paint = new Paint[256];

    public Paint getPaint(int x, int y) {
        for (Paint paint : this.paint) {
            if (paint.x == x && paint.y == y) {
                return paint;
            }
        }
        return null;
    }

    public void paint(int x, int y, EnumChatFormatting color) {
        int index = (y * 16) + x;
        Paint paint = new Paint();
        paint.color = color;
        paint.x = x;
        paint.y = y;
        this.paint[index] = paint;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("facing", facing.ordinal());
        compound.setInteger("paintCount", paint.length);
        NBTTagList list = new NBTTagList();
        for (Paint paint : this.paint) {
            NBTTagCompound paintCompound = new NBTTagCompound();
            if (paint != null) {
                paint.writeToNBT(paintCompound);
            }
            list.appendTag(paintCompound);
        }
        compound.setTag("paint", list);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, PaintedBlock paintedBlock, double x, double y, double z) {
        for (Paint paint : this.paint) {
            if (paint != null) {
                paint.render(mc, this, x, y, z);
            }
        }
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte(facing.ordinal());
        buf.writeByte(paint.length);
        for (Paint paint : this.paint) {
            if (paint != null) {
                paint.encode(buf);
            }
        }
    }

    @Override
    public void decode(ByteBuf buf) {
        facing = EnumFacing.values()[buf.readByte()];
        paint = new Paint[buf.readByte()];
        for (int i = 0; i < paint.length; i++) {
            Paint paint = new Paint();
            paint.decode(buf);
            this.paint[i] = paint;
        }
    }

    public static PaintedFace readFromNBT(NBTTagCompound compound) {
        PaintedFace paintedFace = new PaintedFace();
        paintedFace.facing = EnumFacing.values()[compound.getInteger("facing")];
        paintedFace.paint = new Paint[compound.getInteger("paintCount")];
        NBTTagList list = compound.getTagList("paint", Constants.NBT.TAG_LIST);
        for (int i = 0; i < paintedFace.paint.length; i++) {
            NBTTagCompound paintCompound = list.getCompoundTagAt(i);
            if (!paintCompound.hasNoTags()) {
                paintedFace.paint[i] = Paint.readFromNBT(list.getCompoundTagAt(i));
            }
        }
        return paintedFace;
    }
}
