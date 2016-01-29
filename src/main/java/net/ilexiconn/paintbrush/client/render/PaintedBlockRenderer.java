package net.ilexiconn.paintbrush.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PaintedBlockRenderer extends RenderEntity {
    @Override
    public void doRender(Entity entity, double posX, double posY, double posZ, float yaw, float partialTicks) {
        if (entity instanceof PaintedBlockEntity) {
            PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;

            Minecraft mc = Minecraft.getMinecraft();

            mc.entityRenderer.disableLightmap(partialTicks);
            RenderHelper.disableStandardItemLighting();

            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            for (Paint paint : paintedBlockEntity.paintList) {
                GL11.glPushMatrix();
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
                        px = (paint.posX * 0.0625F) - 0.002F;
                        py = (paint.posY * 0.0625F) - 0.002F;
                        pz = 0;
                        tessellator.addVertex(px, py, pz - 0.001F);
                        tessellator.addVertex(px + 0.0665F, py, pz - 0.001F);
                        tessellator.addVertex(px + 0.0665F, py + 0.0665F, pz - 0.001F);
                        tessellator.addVertex(px, py + 0.0665F, pz - 0.001F);
                        break;
                    case EAST:
                        px = 0;
                        py = (paint.posY * 0.0625F) - 0.002F;
                        pz = (paint.posX * 0.0625F) - 0.002F;
                        tessellator.addVertex(px - 0.001F, py, pz);
                        tessellator.addVertex(px - 0.001F, py + 0.0665F, pz);
                        tessellator.addVertex(px - 0.001F, py + 0.0665F, pz + 0.0665F);
                        tessellator.addVertex(px - 0.001F, py, pz + 0.0665F);
                        break;
                    case SOUTH:
                        px = (paint.posX * 0.0625F) - 0.002F;
                        py = (paint.posY * 0.0625F) - 0.002F;
                        pz = 1.0F;
                        tessellator.addVertex(px, py, pz + 0.001F);
                        tessellator.addVertex(px + 0.0665F, py, pz + 0.001F);
                        tessellator.addVertex(px + 0.0665F, py + 0.0665F, pz + 0.001F);
                        tessellator.addVertex(px, py + 0.0665F, pz + 0.001F);
                        break;
                    case WEST:
                        px = 1.0F;
                        py = (paint.posY * 0.0625F) - 0.002F;
                        pz = (paint.posX * 0.0625F) - 0.002F;
                        tessellator.addVertex(px + 0.001F, py, pz);
                        tessellator.addVertex(px + 0.001F, py + 0.0665F, pz);
                        tessellator.addVertex(px + 0.001F, py + 0.0665F, pz + 0.0665F);
                        tessellator.addVertex(px + 0.001F, py, pz + 0.0665F);
                        break;
                    case UP:
                        px = (paint.posX * 0.0625F) - 0.002F;
                        py = 1.0F;
                        pz = (paint.posY * 0.0625F) - 0.002F;
                        tessellator.addVertex(px, py + 0.001F, pz);
                        tessellator.addVertex(px + 0.0665F, py + 0.001F, pz);
                        tessellator.addVertex(px + 0.0665F, py + 0.001F, pz + 0.0665F);
                        tessellator.addVertex(px, py + 0.001F, pz + 0.0665F);
                        break;
                    case DOWN:
                        px = (paint.posX * 0.0625F) - 0.002F;
                        py = 0;
                        pz = (paint.posY * 0.0625F) - 0.002F;
                        tessellator.addVertex(px, py - 0.001F, pz);
                        tessellator.addVertex(px + 0.0665F, py - 0.001F, pz);
                        tessellator.addVertex(px + 0.0665F, py - 0.001F, pz + 0.0665F);
                        tessellator.addVertex(px, py - 0.001F, pz + 0.0665F);
                        break;
                }
                tessellator.draw();
                GL11.glPopMatrix();
            }

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    public int getColorCode(char character, FontRenderer fontRenderer) {
        return fontRenderer.colorCode["0123456789abcdef".indexOf(character)];
    }
}
