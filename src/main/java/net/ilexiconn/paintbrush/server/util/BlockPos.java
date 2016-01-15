package net.ilexiconn.paintbrush.server.util;

import net.minecraft.nbt.NBTTagCompound;

public class BlockPos {
    private final int x;
    private final int y;
    private final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound pos = new NBTTagCompound();
        pos.setInteger("X", x);
        pos.setInteger("Y", y);
        pos.setInteger("Z", z);
        compound.setTag("Pos", pos);
    }

    public static BlockPos readFromNBT(NBTTagCompound compound) {
        NBTTagCompound pos = compound.getCompoundTag("Pos");
        return new BlockPos(pos.getInteger("X"), pos.getInteger("Y"), pos.getInteger("Z"));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BlockPos) {
            BlockPos pos = (BlockPos) o;
            return pos.x == x && pos.y == y && pos.z == z;
        } else {
            return false;
        }
    }
}
