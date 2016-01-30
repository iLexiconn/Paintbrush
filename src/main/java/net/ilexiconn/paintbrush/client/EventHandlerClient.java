package net.ilexiconn.paintbrush.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.item.PaintScraperItem;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.message.UpdateSizeMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

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
                    if (event.dwheel > 0 && size < 5) {
                        size += event.dwheel / 120;
                    } else if (event.dwheel < 0 && size > 1) {
                        size += event.dwheel / 120;
                    }
                    stack.setItemDamage(getDamage(getColorFromDamage(stack), getInkFromDamage(stack), size, isStackInfinite(stack)));
                    Paintbrush.networkWrapper.sendToServer(new UpdateSizeMessage(stack.getItemDamage()));
                    event.setCanceled(true);
                } else if (item instanceof PaintScraperItem) {
                    int size = stack.getItemDamage();
                    if (event.dwheel > 0 && size < 5) {
                        size += event.dwheel / 120;
                    } else if (event.dwheel < 0 && size > 1) {
                        size += event.dwheel / 120;
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
                    PaintbrushItem paintbrush = (PaintbrushItem) item;
                    int size = getSizeFromDamage(stack);
                    int color = paintbrush.getColorCode(EnumChatFormatting.values()[getColorFromDamage(stack)].getFormattingCode(), mc.fontRenderer);
                    int b = color & 0xFF;
                    int g = (color >>> 8) & 0xFF;
                    int r = (color >>> 16) & 0xFF;

                    GL11.glPushMatrix();
                    GL11.glTranslated(16.0D, 16.0D, 0.0D);
                    GL11.glScalef(2.0F, 2.0F, 2.0F);
                    GL11.glColor4f(r / 255.0F, g / 255.0F, b / 255.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);

                    for (int ring = 0; ring < size; ring++) {
                        for (int i = 0; i < 360; ++i) {
                            double rad = Math.toRadians((double) i);
                            int pX = (int) (-Math.sin(rad) * ring);
                            int pY = (int) (Math.cos(rad) * ring);
                            drawRect(pX, pY, 1, 1);
                        }
                    }

                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glPopMatrix();
                } else if (item instanceof PaintScraperItem) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(16.0D, 16.0D, 0.0D);
                    GL11.glScalef(2.0F, 2.0F, 2.0F);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(GL11.GL_TEXTURE_2D);

                    for (int ring = 0; ring < stack.getItemDamage(); ring++) {
                        for (int i = 0; i < 360; ++i) {
                            double rad = Math.toRadians((double) i);
                            int pX = (int) (-Math.sin(rad) * ring);
                            int pY = (int) (Math.cos(rad) * ring);
                            drawRect(pX, pY, 1, 1);
                        }
                    }

                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public void drawRect(int x, int y, int width, int height) {
        float widthScale = 1.0F / width;
        float heightScale = 1.0F / height;

        double zLevel = 1.0;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) (x), (double) (y + height), zLevel, (double) ((float) (0) * widthScale), (double) ((float) (height) * heightScale));
        tessellator.addVertexWithUV((double) (x + width), (double) (y + height), zLevel, (double) ((float) (width) * widthScale), (double) ((float) (height) * heightScale));
        tessellator.addVertexWithUV((double) (x + width), (double) (y), zLevel, (double) ((float) (width) * widthScale), (double) ((float) (0) * heightScale));
        tessellator.addVertexWithUV((double) (x), (double) (y), zLevel, (double) ((float) (0) * widthScale), (double) ((float) (0) * heightScale));
        tessellator.draw();
    }
}
