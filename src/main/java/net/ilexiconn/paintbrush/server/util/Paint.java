package net.ilexiconn.paintbrush.server.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.client.PaintbrushDataClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
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
        double px;
        double py;
        double pz;

        tessellator.setColorRGBA(r, g, b, 255);
        switch (facing) {
            case NORTH:
                px = pos.x + this.x * 0.0625F;
                py = pos.y + this.y * 0.0625F;
                pz = pos.z;
                tessellator.addVertex(px, py, pz - 0.005F);
                tessellator.addVertex(px + 0.0625F, py, pz - 0.005F);
                tessellator.addVertex(px + 0.0625F, py + 0.0625F, pz - 0.005F);
                tessellator.addVertex(px, py + 0.0625F, pz - 0.005F);
                break;
            case EAST:
                px = pos.x;
                py = pos.y + this.y * 0.0625F;
                pz = pos.z + this.x * 0.0625F;
                tessellator.addVertex(px - 0.005F, py, pz);
                tessellator.addVertex(px - 0.005F, py + 0.0625F, pz);
                tessellator.addVertex(px - 0.005F, py + 0.0625F, pz + 0.0625F);
                tessellator.addVertex(px - 0.005F, py, pz + 0.0625F);
                break;
            case SOUTH:
                px = pos.x + this.x * 0.0625F;
                py = pos.y + this.y * 0.0625F;
                pz = pos.z + 1.0F;
                tessellator.addVertex(px, py, pz + 0.005F);
                tessellator.addVertex(px + 0.0625F, py, pz + 0.005F);
                tessellator.addVertex(px + 0.0625F, py + 0.0625F, pz + 0.005F);
                tessellator.addVertex(px, py + 0.0625F, pz + 0.005F);
                break;
            case WEST:
                px = pos.x + 1.0F;
                py = pos.y + this.y * 0.0625F;
                pz = pos.z + this.x * 0.0625F;
                tessellator.addVertex(px + 0.005F, py, pz);
                tessellator.addVertex(px + 0.005F, py + 0.0625F, pz);
                tessellator.addVertex(px + 0.005F, py + 0.0625F, pz + 0.0625F);
                tessellator.addVertex(px + 0.005F, py, pz + 0.0625F);
                break;
            case UP:
                px = pos.x + this.x * 0.0625F;
                py = pos.y + 1.0F;
                pz = pos.z + this.y * 0.0625F;
                tessellator.addVertex(px, py + 0.005F, pz);
                tessellator.addVertex(px + 0.0625F, py + 0.005F, pz);
                tessellator.addVertex(px + 0.0625F, py + 0.005F, pz + 0.0625F);
                tessellator.addVertex(px, py + 0.005F, pz + 0.0625F);
                break;
            case DOWN:
                px = pos.x + this.x * 0.0625F;
                py = pos.y;
                pz = pos.z + this.y * 0.0625F;
                tessellator.addVertex(px, py - 0.005F, pz);
                tessellator.addVertex(px + 0.0625F, py - 0.005F, pz);
                tessellator.addVertex(px + 0.0625F, py - 0.005F, pz + 0.0625F);
                tessellator.addVertex(px, py - 0.005F, pz + 0.0625F);
                break;
        }
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
        buf.writeInt(data);
    }

    @Override
    public Paint decode(ByteBuf buf) {
        int data = buf.readInt();
        color = EnumChatFormatting.values()[data & 0B1111];
        x = (data >>> 4) & 0B1111;
        y = (data >>> 8) & 0B1111;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateClient(Minecraft mc, EntityPlayer player, Object... data) {
        MovingObjectPosition object = mc.objectMouseOver;
        if (object != null && object.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = new BlockPos(object.blockX, object.blockY, object.blockZ);
            EnumFacing facing = EnumFacing.values()[object.sideHit];
            PaintbrushDataClient.addPaint(PaintbrushDataClient.getPaintedBlock(pos).getPaintedFace(facing), this);
        }
    }

    @Override
    public void updateServer(MinecraftServer mc, EntityPlayer player, Object... data) {

    }

    @SideOnly(Side.CLIENT)
    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
