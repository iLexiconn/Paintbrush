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

public class PaintedFace implements Util<PaintedFace> {
    public EnumFacing facing;
    public List<Paint> paintList = Lists.newArrayList();

    public Paint getPaint(int x, int y) {
        for (Paint paint : paintList) {
            if (paint.x == x && paint.y == y) {
                return paint;
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Tessellator tessellator, double x, double y, double z, Object... data) {
        for (Paint paint : paintList) {
            paint.render(mc, tessellator, x, y, z, data[0], facing);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("facing", facing.ordinal());
        NBTTagList list = new NBTTagList();
        for (Paint paint : paintList) {
            NBTTagCompound paintCompound = new NBTTagCompound();
            paint.writeToNBT(paintCompound);
            list.appendTag(paintCompound);
        }
        compound.setTag("paint", list);
    }

    @Override
    public PaintedFace readFromNBT(NBTTagCompound compound) {
        facing = EnumFacing.values()[compound.getInteger("facing")];
        paintList = Lists.newArrayList();
        NBTTagList list = compound.getTagList("paint", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            paintList.add(new Paint().readFromNBT(list.getCompoundTagAt(i)));
        }
        return this;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte(facing.ordinal());
        buf.writeByte(paintList.size());
        for (Paint paint : paintList) {
            paint.encode(buf);
        }
    }

    @Override
    public PaintedFace decode(ByteBuf buf) {
        facing = EnumFacing.values()[buf.readByte()];
        paintList = Lists.newArrayList();
        for (int i = 0; i < buf.readByte(); i++) {
            paintList.add(new Paint().decode(buf));
        }
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateClient(Minecraft mc, Object... data) {
        MovingObjectPosition object = mc.objectMouseOver;
        if (object != null && object.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = new BlockPos(object.blockX, object.blockY, object.blockZ);
            PaintbrushDataClient.addPaintedFace(PaintbrushDataClient.getPaintedBlock(pos), this);
            for (Paint paint : paintList) {
                paint.updateClient(mc);
            }
        }
    }
}
