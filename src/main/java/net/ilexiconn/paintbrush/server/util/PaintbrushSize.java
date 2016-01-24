package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class PaintbrushSize implements Util<PaintbrushSize> {
    private int size;

    public PaintbrushSize() {

    }

    public PaintbrushSize(int size) {
        this.size = size;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Tessellator tessellator, double x, double y, double z, Object... data) {

    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {

    }

    @Override
    public PaintbrushSize readFromNBT(NBTTagCompound compound) {
        return this;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(size);
    }

    @Override
    public PaintbrushSize decode(ByteBuf buf) {
        size = buf.readInt();
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateClient(Minecraft mc, EntityPlayer player, Object... data) {

    }

    @Override
    public void updateServer(MinecraftServer mc, EntityPlayer player, Object... data) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof PaintbrushItem) {
            PaintbrushItem item = (PaintbrushItem) stack.getItem();
            item.setDamage(stack, item.getDamage(item.getColorFromDamage(stack), item.getInkFromDamage(stack), size, item.isStackInfinite(stack)));
        }
    }
}
