package net.ilexiconn.paintbrush.server.util;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.client.PaintbrushDataClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
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
        NBTTagList list = compound.getTagList("faces", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            paintedFaceList.add(new PaintedFace().readFromNBT(list.getCompoundTagAt(i)));
        }
        return this;
    }

    @Override
    public void encode(ByteBuf buf) {
        pos.encode(buf);
        buf.writeByte(paintedFaceList.size());
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
        for (int i = 0; i < size; i++) {
            paintedFaceList.add(new PaintedFace().decode(buf));
        }
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateClient(Minecraft mc, EntityPlayer player, Object... data) {
        PaintbrushDataClient.addPaintedBlock(this);
        if (data.length == 0 || (boolean) data[0]) {
            for (PaintedFace paintedFace : paintedFaceList) {
                paintedFace.updateClient(mc, player);
            }
        }
    }

    @Override
    public void updateServer(MinecraftServer mc, EntityPlayer player, Object... data) {

    }
}
