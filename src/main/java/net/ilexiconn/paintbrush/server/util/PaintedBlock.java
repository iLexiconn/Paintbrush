package net.ilexiconn.paintbrush.server.util;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.client.PaintbrushDataClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class PaintedBlock implements Util<PaintedBlock> {
    public BlockPos pos;
    public List<PaintedFace> paintedFaceList = Lists.newArrayList();

    public PaintedFace getPaintedFace(EnumFacing facing) {
        for (PaintedFace paintedFace : paintedFaceList) {
            if (paintedFace.facing == facing) {
                return paintedFace;
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Tessellator tessellator, double x, double y, double z, Object... data) {
        for (PaintedFace paintedFace : paintedFaceList) {
            paintedFace.render(mc, tessellator, x, y, z, pos);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        pos.writeToNBT(compound);
        compound.setInteger("faceCount", paintedFaceList.size());
        NBTTagList list = new NBTTagList();
        for (PaintedFace paintedFace : paintedFaceList) {
            NBTTagCompound faceCompound = new NBTTagCompound();
            paintedFace.writeToNBT(faceCompound);
            list.appendTag(faceCompound);
        }
        compound.setTag("faces", list);
    }

    @Override
    public PaintedBlock readFromNBT(NBTTagCompound compound) {
        pos = new BlockPos().readFromNBT(compound);
        paintedFaceList = Lists.newArrayList();
        NBTTagList list = compound.getTagList("paint", Constants.NBT.TAG_LIST);
        for (int i = 0; i < compound.getInteger("faceCount"); i++) {
            paintedFaceList.add(new PaintedFace().readFromNBT(list.getCompoundTagAt(i)));
        }
        return this;
    }

    @Override
    public void encode(ByteBuf buf) {
        pos.encode(buf);
        buf.writeByte(paintedFaceList.size());
        System.out.println("Encoding c:" + paintedFaceList.size());
        for (PaintedFace paintedFace : paintedFaceList) {
            paintedFace.encode(buf);
        }
    }

    @Override
    public PaintedBlock decode(ByteBuf buf) {
        pos = new BlockPos();
        pos.decode(buf);
        paintedFaceList = Lists.newArrayList();
        int size = buf.readByte();
        System.out.println("Decoding c:" + size);
        for (int i = 0; i < size; i++) {
            paintedFaceList.add(new PaintedFace().decode(buf));
        }
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateClient(Minecraft mc) {
        MovingObjectPosition object = mc.objectMouseOver;
        if (object.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = object.blockX;
            int y = object.blockY;
            int z = object.blockZ;
            BlockPos pos = new BlockPos(x, y, z);
            PaintedBlock paintedBlock = new PaintedBlock();
            paintedBlock.pos = pos;
            PaintbrushDataClient.addPaintedBlock(paintedBlock);
        }
    }
}
