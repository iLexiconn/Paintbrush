package net.ilexiconn.paintbrush.client;

import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.item.PaintScraperItem;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.message.UpdateSizeMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.ilexiconn.paintbrush.server.item.PaintbrushItem.*;

@SideOnly(Side.CLIENT)
public class EventHandlerClient {
    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        EntityPlayer player = mc.thePlayer;
        if (event.dwheel != 0 && player != null && player.isSneaking()) {
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof PaintbrushItem) {
                    int size = getSizeFromDamage(stack);
                    if (event.dwheel > 0 && size < 7) {
                        size += event.dwheel / 120;
                    } else if (event.dwheel < 0 && size > 1) {
                        size += event.dwheel / 120;
                    }
                    if (size > 8) {
                        size = 8;
                    } else if (size < 1) {
                        size = 1;
                    }
                    stack.setItemDamage(getDamage(getColorFromDamage(stack), size));
                    Paintbrush.networkWrapper.sendToServer(new UpdateSizeMessage(stack.getItemDamage()));
                    event.setCanceled(true);
                } else if (item instanceof PaintScraperItem) {
                    int size = stack.getItemDamage();
                    if (event.dwheel > 0 && size < 7) {
                        size += event.dwheel / 120;
                    } else if (event.dwheel < 0 && size > 1) {
                        size += event.dwheel / 120;
                    }
                    if (size > 8) {
                        size = 8;
                    } else if (size < 1) {
                        size = 1;
                    }
                    stack.setItemDamage(size);
                    Paintbrush.networkWrapper.sendToServer(new UpdateSizeMessage(size));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        EntityPlayer player = mc.thePlayer;
        if (player != null && event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof PaintbrushItem) {
                    int size = getSizeFromDamage(stack);
                    int color = mc.fontRendererObj.getColorCode(EnumChatFormatting.values()[getColorFromDamage(stack)].formattingCode);
                    int b = color & 0xFF;
                    int g = (color >>> 8) & 0xFF;
                    int r = (color >>> 16) & 0xFF;

                    drawPaintPreview(size, b, g, r);
                } else if (item instanceof PaintScraperItem) {
                    drawPaintPreview(stack.getItemDamage(), 255, 255, 255);
                }
            }
        }
    }

    private void drawPaintPreview(int size, int b, int g, int r) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(16.0D, 16.0D, 0.0D);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.color(r / 255.0F, g / 255.0F, b / 255.0F, 1.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();

        for (int ring = 0; ring < size; ring++) {
            for (int i = 0; i < 360; ++i) {
                double rad = Math.toRadians((double) i);
                int pX = (int) (-Math.sin(rad) * ring);
                int pY = (int) (Math.cos(rad) * ring);
                drawRect(pX, pY, 1, 1);
            }
        }

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawRect(int left, int top, int right, int bottom) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double) left, (double) bottom, 0.0D).endVertex();
        worldrenderer.pos((double) right, (double) bottom, 0.0D).endVertex();
        worldrenderer.pos((double) right, (double) top, 0.0D).endVertex();
        worldrenderer.pos((double) left, (double) top, 0.0D).endVertex();
        tessellator.draw();
    }
}
