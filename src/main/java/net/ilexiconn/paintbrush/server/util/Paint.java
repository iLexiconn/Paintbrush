package net.ilexiconn.paintbrush.server.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;

public class Paint {
    private final EnumChatFormatting color;
    private final int x;
    private final int y;
    private final EnumFacing facing;
    private final BlockPos pos;

    public Paint(EnumChatFormatting color, int x, int y, EnumFacing facing, BlockPos pos) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.facing = facing;
        this.pos = pos;
    }

    public EnumChatFormatting getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Color", color.ordinal());
        compound.setInteger("X", x);
        compound.setInteger("Y", y);
        compound.setInteger("Facing", facing.ordinal());
        pos.writeToNBT(compound);
    }

    public static Paint readFromNBT(NBTTagCompound compound) {
        return new Paint(EnumChatFormatting.values()[compound.getInteger("Color")], compound.getInteger("X"), compound.getInteger("Y"), EnumFacing.values()[compound.getInteger("Facing")], BlockPos.readFromNBT(compound));
    }
}
