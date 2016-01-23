package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.client.PaintbrushDataClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;

public class Paint implements Util<Paint> {
    public EnumChatFormatting color;
    public int x;
    public int y;

    @Override
    @SideOnly(Side.CLIENT)
    public void render(Minecraft mc, Tessellator tessellator, double x, double y, double z, Object... data) {
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, -z);
        tessellator.startDrawingQuads();
        BlockPos pos = (BlockPos) data[0];
        EnumFacing facing = (EnumFacing) data[1];
        int hex = getColorCode(color.getFormattingCode(), Minecraft.getMinecraft().fontRenderer);
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        double px = pos.x + this.x * 0.0625F;
        double py = pos.y + this.y * 0.0625F;
        double pz = pos.z;
        System.out.println("Rendering: " + pos + ", face(" + facing + "), offset(" + this.x + "," + this.y + "), color(" + color.name() + ")");

        tessellator.setColorRGBA(r, g, b, 255);
        tessellator.addVertex(px, py, pz - 0.01F);
        tessellator.addVertex(px + 0.0625F, py, pz - 0.01F);
        tessellator.addVertex(px + 0.0625F, py + 0.0625F, pz - 0.01F);
        tessellator.addVertex(px, py + 0.0625F, pz - 0.01F);
        tessellator.draw();
        GL11.glPopMatrix();
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
        int data = ((color.ordinal() & 0B1111) | ((x & 0B1111) << 4) | ((y & 0B1111) << 8));
        System.out.println("Encoding c:" + color.ordinal() + ",x:" + x + ",y:" + y + " to " + data);
        buf.writeInt(data);
    }

    @Override
    public Paint decode(ByteBuf buf) {
        int data = buf.readInt();
        color = EnumChatFormatting.values()[data & 0B1111];
        x = (data >>> 4) & 0B1111;
        y = (data >>> 8) & 0B1111;
        System.out.println("Decoding " + data + " to c:" + color.ordinal() + ",x:" + x + ",y:" + y);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateClient(Minecraft mc) {
        MovingObjectPosition object = mc.objectMouseOver;
        if (object.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = object.blockX;
            int y = object.blockY;
            int z = object.blockZ;
            BlockPos pos = new BlockPos(x, y, z);
            EnumFacing facing = EnumFacing.values()[object.sideHit];
            PaintbrushDataClient.addPaint(PaintbrushDataClient.getPaintedBlock(pos).getPaintedFace(facing), this.x, this.y, color);
        }
    }

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
