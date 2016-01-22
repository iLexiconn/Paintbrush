package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class Paint implements Util<Paint> {
    public EnumChatFormatting color;
    public int x;
    public int y;

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Tessellator tessellator, double x, double y, double z, Object... data) {
        tessellator.startDrawingQuads();
        GL11.glTranslated(x, y, z);
        BlockPos pos = (BlockPos) data[0];
        EnumFacing facing = (EnumFacing) data[1];
        int hex = getColorCode(color.getFormattingCode(), Minecraft.getMinecraft().fontRenderer);
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        double px = pos.x + x * 0.0625F;
        double py = pos.y + y * 0.0625F;
        double pz = pos.z;

        tessellator.setColorRGBA(r, g, b, 255);
        tessellator.addVertex(px, py, pz - 0.01F);
        tessellator.addVertex(px + 0.0625F, py, pz - 0.01F);
        tessellator.addVertex(px + 0.0625F, py + 0.0625F, pz - 0.01F);
        tessellator.addVertex(px, py + 0.0625F, pz - 0.01F);
        tessellator.draw();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("color", color.ordinal());
        compound.setInteger("x", x);
        compound.setInteger("y", y);
    }

    @Override
    public Paint readFromNBT(NBTTagCompound compound) {
        color = EnumChatFormatting.values()[compound.getInteger("color")];
        x = compound.getInteger("x");
        y = compound.getInteger("y");
        return this;
    }

    @Override
    public void encode(ByteBuf buf) {
        System.out.println("Encoding c:" + color.ordinal() + ",x:" + x + ",y:" + y + " to " + ((color.ordinal() & 0B1111) | ((x & 0B1111) << 4) | ((y & 0B1111) << 10)));
        buf.writeByte((color.ordinal() & 0B1111) | ((x & 0B1111) << 4) | ((y & 0B1111) << 10));
    }

    @Override
    public Paint decode(ByteBuf buf) {
        byte data = buf.readByte();
        color = EnumChatFormatting.values()[data & 0B1111];
        x = data >>> 4 & 0B1111;
        y = data >>> 8 & 0B1111;
        System.out.println("Decoding " + data + " to c:" + color.ordinal() + ",x:" + x + ",y:" + y);
        return this;
    }

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
