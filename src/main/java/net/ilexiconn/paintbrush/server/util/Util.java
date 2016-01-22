package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;

public interface Util<SELF extends Util> {
    @SideOnly(Side.CLIENT)
    void render(Minecraft mc, Tessellator tessellator, double x, double y, double z, Object... data);

    void writeToNBT(NBTTagCompound compound);

    SELF readFromNBT(NBTTagCompound compound);

    void encode(ByteBuf buf);

    SELF decode(ByteBuf buf);

    @SideOnly(Side.CLIENT)
    void updateClient(Minecraft mc);
}
