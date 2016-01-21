package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

public class Paint implements Util<PaintedFace> {
    public EnumChatFormatting color;
    public int x;
    public int y;

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("color", color.ordinal());
        compound.setInteger("x", x);
        compound.setInteger("y", y);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, PaintedFace paintedFace, double x, double y, double z) {

    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte((color.ordinal() & 0B1111) | ((x & 0B1111) << 4) | ((y & 0B1111) << 10));
    }

    @Override
    public void decode(ByteBuf buf) {
        byte data = buf.readByte();
        color = EnumChatFormatting.values()[data & 0B1111];
        x = data >>> 4 & 0B1111;
        y = data >>> 8 & 0B1111;
    }

    public static Paint readFromNBT(NBTTagCompound compound) {
        Paint paint = new Paint();
        paint.color = EnumChatFormatting.values()[compound.getInteger("color")];
        paint.x = compound.getInteger("x");
        paint.y = compound.getInteger("y");
        return paint;
    }
}
