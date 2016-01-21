package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public class BlockPos implements Util {
    public int x;
    public int y;
    public int z;

    public BlockPos() {

    }

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("x", x);
        compound.setInteger("y", y);
        compound.setInteger("z", z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Util paint, double x, double y, double z) {

    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void decode(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    public static BlockPos readFromNBT(NBTTagCompound compound) {
        return new BlockPos(compound.getInteger("x"), compound.getInteger("y"), compound.getInteger("z"));
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
