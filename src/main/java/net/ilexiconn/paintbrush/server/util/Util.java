package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

public interface Util<I extends Util> {
    void writeToNBT(NBTTagCompound compound);

    @SideOnly(Side.CLIENT)
    void render(Minecraft mc, I i, double x, double y, double z);

    void encode(ByteBuf buf);

    void decode(ByteBuf buf);
}
