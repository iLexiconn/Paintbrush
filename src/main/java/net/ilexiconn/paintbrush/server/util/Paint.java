package net.ilexiconn.paintbrush.server.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;

public class Paint {
    public EnumFacing facing;
    public int posX;
    public int posY;
    public EnumChatFormatting color;

    public Paint(EnumFacing facing, int posX, int posY, EnumChatFormatting color) {
        this.facing = facing;
        this.posX = posX;
        this.posY = posY;
        this.color = color;
    }

    public static Paint readFromNBT(NBTTagCompound compound) {
        EnumFacing facing = EnumFacing.values()[compound.getInteger("Facing")];
        int posX = compound.getInteger("PosX");
        int posY = compound.getInteger("PosY");
        EnumChatFormatting color = EnumChatFormatting.values()[compound.getInteger("Color")];
        return new Paint(facing, posX, posY, color);
    }

    public static Paint decode(ByteBuf buf) {
        int data = buf.readInt();
        EnumFacing facing = EnumFacing.values()[data & 0B111];
        int posX = (data >>> 3) & 0B1111;
        int posY = (data >>> 7) & 0B1111;
        EnumChatFormatting color = EnumChatFormatting.values()[(data >>> 11) & 0B1111];
        return new Paint(facing, posX, posY, color);
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Facing", facing.ordinal());
        compound.setInteger("PosX", posX);
        compound.setInteger("PosY", posY);
        compound.setInteger("Color", color.ordinal());
    }

    public void encode(ByteBuf buf) {
        buf.writeInt(((facing.ordinal() & 0B111) | ((posX & 0B1111) << 3) | ((posY & 0B1111) << 7) | ((color.ordinal() & 0B1111) << 11)));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Paint) {
            Paint paint = (Paint) obj;
            return paint.facing == facing && paint.posX == posX && paint.posY == posY && paint.color == color;
        } else {
            return false;
        }
    }
}
