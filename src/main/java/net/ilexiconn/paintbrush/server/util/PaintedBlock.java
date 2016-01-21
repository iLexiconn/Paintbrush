package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

public class PaintedBlock implements Util {
    public BlockPos pos;
    public PaintedFace[] paintedFaces;

    public PaintedFace getPaintedFace(EnumFacing facing) {
        for (PaintedFace paintedFace : paintedFaces) {
            if (paintedFace.facing == facing) {
                return paintedFace;
            }
        }
        return null;
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
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Util paint, double x, double y, double z) {
        for (PaintedFace paintedFace : paintedFaces) {
            if (paintedFace != null) {
                paintedFace.render(mc, this, x, y, z);
            }
        }
    }

    @Override
    public void encode(ByteBuf buf) {
        pos.encode(buf);
        buf.writeByte(paintedFaces.length);
        for (PaintedFace paintedFace : paintedFaces) {
            if (paintedFace != null) {
                paintedFace.encode(buf);
            }
        }
    }

    @Override
    public void decode(ByteBuf buf) {
        pos = new BlockPos();
        pos.decode(buf);
        paintedFaces = new PaintedFace[buf.readByte()];
        for (int i = 0; i < paintedFaces.length; i++) {
            PaintedFace paintedFace = new PaintedFace();
            paintedFace.decode(buf);
            paintedFaces[i] = paintedFace;
        }
    }

    public static PaintedBlock readFromNBT(NBTTagCompound compound) {
        PaintedBlock paintedBlock = new PaintedBlock();
        paintedBlock.pos = BlockPos.readFromNBT(compound);
        paintedBlock.paintedFaces = new PaintedFace[compound.getInteger("faceCount")];
        NBTTagList list = compound.getTagList("paint", Constants.NBT.TAG_LIST);
        for (int i = 0; i < paintedBlock.paintedFaces.length; i++) {
            NBTTagCompound faceCompound = list.getCompoundTagAt(i);
            if (!faceCompound.hasNoTags()) {
                paintedBlock.paintedFaces[i] = PaintedFace.readFromNBT(faceCompound);
            }
        }
        return paintedBlock;
    }
}
