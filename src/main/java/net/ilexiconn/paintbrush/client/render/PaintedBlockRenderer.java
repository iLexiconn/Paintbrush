package net.ilexiconn.paintbrush.client.render;

import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PaintedBlockRenderer extends Render<PaintedBlockEntity> {
    public PaintedBlockRenderer(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void doRender(PaintedBlockEntity entity, double posX, double posY, double posZ, float yaw, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.disableCull();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glNormal3f(0.5F, 0.5F, 0.5F);
        AxisAlignedBB bounds = entity.worldObj.getBlockState(entity.blockPos).getBlock().getSelectedBoundingBox(entity.worldObj, entity.blockPos);

        for (Paint paint : entity.paintList) {
            GlStateManager.pushMatrix();

            int i = entity.getBrightnessForFace(paint.facing);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            int hex = mc.fontRendererObj.getColorCode(paint.color.formattingCode);
            int r = (hex & 0xFF0000) >> 16;
            int g = (hex & 0xFF00) >> 8;
            int b = (hex & 0xFF);

            GlStateManager.translate(posX - 0.5F, posY, posZ - 0.5F);

            double px;
            double py;
            double pz;

            switch (paint.facing) {
                case NORTH:
                    px = (paint.posX * 0.0625F);
                    py = (paint.posY * 0.0625F);
                    pz = bounds.minZ - entity.blockPos.getZ();
                    worldRenderer.pos(px, py, pz - 0.005F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.0625F, py, pz - 0.005F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.0625F, py + 0.0625F, pz - 0.005F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px, py + 0.0625F, pz - 0.005F).color(r, g, b, 255).endVertex();
                    break;
                case EAST:
                    px = bounds.maxX - entity.blockPos.getX();
                    py = (paint.posY * 0.0625F);
                    pz = (paint.posX * 0.0625F);
                    worldRenderer.pos(px + 0.005F, py, pz).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.005F, py + 0.0625F, pz).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.005F, py + 0.0625F, pz + 0.0625F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.005F, py, pz + 0.0625F).color(r, g, b, 255).endVertex();
                    break;
                case SOUTH:
                    px = (paint.posX * 0.0625F);
                    py = (paint.posY * 0.0625F);
                    pz = bounds.maxZ - entity.blockPos.getZ();
                    worldRenderer.pos(px, py, pz + 0.005F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.0625F, py, pz + 0.005F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.0625F, py + 0.0625F, pz + 0.005F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px, py + 0.0625F, pz + 0.005F).color(r, g, b, 255).endVertex();
                    break;
                case WEST:
                    px = bounds.minX - entity.blockPos.getX();
                    py = (paint.posY * 0.0625F);
                    pz = (paint.posX * 0.0625F);
                    worldRenderer.pos(px - 0.005F, py, pz).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px - 0.005F, py + 0.0625F, pz).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px - 0.005F, py + 0.0625F, pz + 0.0625F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px - 0.005F, py, pz + 0.0625F).color(r, g, b, 255).endVertex();
                    break;
                case UP:
                    px = (paint.posX * 0.0625F);
                    py = bounds.maxY - entity.blockPos.getY();
                    pz = (paint.posY * 0.0625F);
                    worldRenderer.pos(px, py + 0.005F, pz).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.0625F, py + 0.005F, pz).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.0625F, py + 0.005F, pz + 0.0625F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px, py + 0.005F, pz + 0.0625F).color(r, g, b, 255).endVertex();
                    break;
                case DOWN:
                    px = (paint.posX * 0.0625F);
                    py = bounds.minY - entity.blockPos.getY();
                    pz = (paint.posY * 0.0625F);
                    worldRenderer.pos(px, py - 0.005F, pz).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.0625F, py - 0.005F, pz).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px + 0.0625F, py - 0.005F, pz + 0.0625F).color(r, g, b, 255).endVertex();
                    worldRenderer.pos(px, py - 0.005F, pz + 0.0625F).color(r, g, b, 255).endVertex();
                    break;
            }
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
    }

    @Override
    protected ResourceLocation getEntityTexture(PaintedBlockEntity entity) {
        return null;
    }
}