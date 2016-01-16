package net.ilexiconn.paintbrush.client;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    private static List<Paint> paintList = Lists.newArrayList();

    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        Tessellator tessellator = Tessellator.instance;
        for (Paint paint : paintList) {
            tessellator.startDrawingQuads();
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            glDisable(GL_CULL_FACE);
            BlockPos pos = paint.getPos();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            tessellator.addVertex(x, y, z);
            tessellator.addVertex(x + 1, y, z);
            tessellator.addVertex(x + 1, y + 1, z);
            tessellator.addVertex(x, y + 1, z);
            tessellator.draw();
        }
    }

    public static void addPaint(Paint paint) {
        System.out.println("Adding paint on CLIENT");
        paintList.add(paint);
    }

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (event.dwheel != 0 && player != null && player.isSneaking()) {
            System.out.println(event.dwheel);
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof PaintbrushItem) {
                    PaintbrushItem paintbrush = (PaintbrushItem) item;
                    int size = paintbrush.getSizeFromDamage(stack);
                    if (event.dwheel > 0 && size < 5) {
                        size += event.dwheel / 120;
                    } else if (event.dwheel < 0 && size > 1) {
                        size += event.dwheel / 120;
                    }
                    paintbrush.setDamage(stack, paintbrush.getDamage(paintbrush.getColorFromDamage(stack), paintbrush.getInkFromDamage(stack), size));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null) {
            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof PaintbrushItem) {
                    PaintbrushItem paintbrush = (PaintbrushItem) item;
                    glPushMatrix();
                    glTranslated(16.0D, 16.0D, 0.0D);
                    glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
                    glDisable(GL_TEXTURE_2D);
                    glEnable(GL_LINE_SMOOTH);
                    glBegin(GL_LINE_LOOP);
                    for (int i = 0; i < 360; i++) {
                        double degInRad = Math.toRadians(i);
                        glVertex2d(Math.cos(degInRad) * paintbrush.getSizeFromDamage(stack), Math.sin(degInRad) * paintbrush.getSizeFromDamage(stack));
                    }
                    glEnd();
                    glEnable(GL_TEXTURE_2D);
                    glDisable(GL_LINE_SMOOTH);
                    glPopMatrix();
                }
            }
        }
    }
}
