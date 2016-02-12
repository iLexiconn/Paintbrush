package net.ilexiconn.paintbrush.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PaintedBlockRenderer extends Render {
    @Override
    public void doRender(Entity entity, double posX, double posY, double posZ, float yaw, float partialTicks) {
        if (entity instanceof PaintedBlockEntity) {
            PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;

            Minecraft mc = Minecraft.getMinecraft();

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glNormal3f(0.5F, 0.5F, 0.5F);
            AxisAlignedBB bounds = entity.worldObj.getBlock(paintedBlockEntity.blockX, paintedBlockEntity.blockY, paintedBlockEntity.blockZ).getSelectedBoundingBoxFromPool(paintedBlockEntity.worldObj, paintedBlockEntity.blockX, paintedBlockEntity.blockY, paintedBlockEntity.blockZ);

            for (Paint paint : paintedBlockEntity.paintList) {
                GL11.glPushMatrix();

                int i = paintedBlockEntity.getBrightnessForFace(paint.facing);
                int j = i % 65536;
                int k = i / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);

                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                int hex = getColorCode(paint.color.getFormattingCode(), mc.fontRenderer);
                int r = (hex & 0xFF0000) >> 16;
                int g = (hex & 0xFF00) >> 8;
                int b = (hex & 0xFF);

                GL11.glTranslated(posX - 0.5F, posY, posZ - 0.5F);

                double px;
                double py;
                double pz;

                tessellator.setColorRGBA(r, g, b, 255);
                switch (paint.facing) {
                    case NORTH:
                        px = (paint.posX * 0.0625F);
                        py = (paint.posY * 0.0625F);
                        pz = 0.5F + bounds.minZ - paintedBlockEntity.posZ;
                        tessellator.addVertex(px, py, pz - 0.005F);
                        tessellator.addVertex(px + 0.0625F, py, pz - 0.005F);
                        tessellator.addVertex(px + 0.0625F, py + 0.0625F, pz - 0.005F);
                        tessellator.addVertex(px, py + 0.0625F, pz - 0.005F);
                        break;
                    case EAST:
                        px = 0.5F + bounds.minX - paintedBlockEntity.posX;
                        py = (paint.posY * 0.0625F);
                        pz = (paint.posX * 0.0625F);
                        tessellator.addVertex(px - 0.005F, py, pz);
                        tessellator.addVertex(px - 0.005F, py + 0.0625F, pz);
                        tessellator.addVertex(px - 0.005F, py + 0.0625F, pz + 0.0625F);
                        tessellator.addVertex(px - 0.005F, py, pz + 0.0625F);
                        break;
                    case SOUTH:
                        px = (paint.posX * 0.0625F);
                        py = (paint.posY * 0.0625F);
                        pz = 0.5F + bounds.maxZ - paintedBlockEntity.posZ;
                        tessellator.addVertex(px, py, pz + 0.005F);
                        tessellator.addVertex(px + 0.0625F, py, pz + 0.005F);
                        tessellator.addVertex(px + 0.0625F, py + 0.0625F, pz + 0.005F);
                        tessellator.addVertex(px, py + 0.0625F, pz + 0.005F);
                        break;
                    case WEST:
                        px = 0.5F + bounds.maxX - paintedBlockEntity.posX;
                        py = (paint.posY * 0.0625F);
                        pz = (paint.posX * 0.0625F);
                        tessellator.addVertex(px + 0.005F, py, pz);
                        tessellator.addVertex(px + 0.005F, py + 0.0625F, pz);
                        tessellator.addVertex(px + 0.005F, py + 0.0625F, pz + 0.0625F);
                        tessellator.addVertex(px + 0.005F, py, pz + 0.0625F);
                        break;
                    case UP:
                        px = (paint.posX * 0.0625F);
                        py = bounds.maxY - paintedBlockEntity.posY;
                        pz = (paint.posY * 0.0625F);
                        tessellator.addVertex(px, py + 0.005F, pz);
                        tessellator.addVertex(px + 0.0625F, py + 0.005F, pz);
                        tessellator.addVertex(px + 0.0625F, py + 0.005F, pz + 0.0625F);
                        tessellator.addVertex(px, py + 0.005F, pz + 0.0625F);
                        break;
                    case DOWN:
                        px = (paint.posX * 0.0625F);
                        py = bounds.minY - paintedBlockEntity.posY;
                        pz = (paint.posY * 0.0625F);
                        tessellator.addVertex(px, py - 0.005F, pz);
                        tessellator.addVertex(px + 0.0625F, py - 0.005F, pz);
                        tessellator.addVertex(px + 0.0625F, py - 0.005F, pz + 0.0625F);
                        tessellator.addVertex(px, py - 0.005F, pz + 0.0625F);
                        break;
                }
                tessellator.draw();
                GL11.glPopMatrix();
            }

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
