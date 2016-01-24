package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;

public class BlockPos implements Util<BlockPos> {
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

    public BlockPos offset(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Tessellator tessellator, double x, double y, double z, Object... data) {

    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("x", x);
        compound.setInteger("y", y);
        compound.setInteger("z", z);
    }

    @Override
    public BlockPos readFromNBT(NBTTagCompound compound) {
        x = compound.getInteger("x");
        y = compound.getInteger("y");
        z = compound.getInteger("z");
        return this;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public BlockPos decode(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateClient(Minecraft mc, Object... data) {

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

    @Override
    public String toString() {
        return "pos(" + x + "," + y + "," + z + ")";
    }
}
