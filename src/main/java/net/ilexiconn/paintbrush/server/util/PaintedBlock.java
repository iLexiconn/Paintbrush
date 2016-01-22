package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

public class PaintedBlock implements Util<Util, PaintedBlock> {
    public BlockPos pos;
    public PaintedFace[] paintedFaces = new PaintedFace[6];

    public PaintedBlock() {
        for (int i = 0; i < 6; i++) {
            paintedFaces[i] = new PaintedFace();
            paintedFaces[i].facing = EnumFacing.values()[i];
        }
    }

    public PaintedFace getPaintedFace(EnumFacing facing) {
        for (PaintedFace paintedFace : paintedFaces) {
            if (facing.equals(paintedFace.facing)) {
                return paintedFace;
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Util paint, double x, double y, double z) {
        for (PaintedFace paintedFace : paintedFaces) {
            if (paintedFace != null) {
                paintedFace.render(mc, this, x, y, z);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        pos.writeToNBT(compound);
        compound.setInteger("faceCount", paintedFaces.length);
        NBTTagList list = new NBTTagList();
        for (PaintedFace paintedFace : paintedFaces) {
            NBTTagCompound faceCompound = new NBTTagCompound();
            if (paintedFace != null) {
                paintedFace.writeToNBT(faceCompound);
            }
            list.appendTag(faceCompound);
        }
        compound.setTag("faces", list);
    }

    @Override
    public PaintedBlock readFromNBT(NBTTagCompound compound) {
        pos = new BlockPos().readFromNBT(compound);
        paintedFaces = new PaintedFace[compound.getInteger("faceCount")];
        NBTTagList list = compound.getTagList("paint", Constants.NBT.TAG_LIST);
        for (int i = 0; i < paintedFaces.length; i++) {
            NBTTagCompound faceCompound = list.getCompoundTagAt(i);
            if (!faceCompound.hasNoTags()) {
                paintedFaces[i] = new PaintedFace().readFromNBT(faceCompound);
            }
        }
        return this;
    }

    @Override
    public void encode(ByteBuf buf) {
        pos.encode(buf);
        buf.writeByte(paintedFaces.length);
        System.out.println("Encoding c:" + paintedFaces.length);
        for (PaintedFace paintedFace : paintedFaces) {
            if (paintedFace != null) {
                paintedFace.encode(buf);
            }
        }
    }

    @Override
    public PaintedBlock decode(ByteBuf buf) {
        pos = new BlockPos();
        pos.decode(buf);
        paintedFaces = new PaintedFace[buf.readByte()];
        System.out.println("Decoding c:" + paintedFaces.length);
        for (int i = 0; i < paintedFaces.length; i++) {
            paintedFaces[i] = new PaintedFace().decode(buf);
        }
        return this;
    }
}
