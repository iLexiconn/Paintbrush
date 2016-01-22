package net.ilexiconn.paintbrush.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.util.PaintedBlock;
import net.ilexiconn.paintbrush.server.world.PaintbrushData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    public Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        EntityLivingBase view = mc.renderViewEntity;
        double dX = view.lastTickPosX + (view.posX - view.lastTickPosX) * (double) event.partialTicks;
        double dY = view.lastTickPosY + (view.posY - view.lastTickPosY) * (double) event.partialTicks;
        double dZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * (double) event.partialTicks;
        glDisable(GL_CULL_FACE);
        glDisable(GL_TEXTURE_2D);

        glColor4f(1, 1, 1, 1);
        glPushMatrix();

        PaintbrushData data = PaintbrushData.get(mc.theWorld);

        if (data != null) {
            for (PaintedBlock paintedBlock : data.getPaintedBlocks()) {
                if (paintedBlock != null) {
                    paintedBlock.render(mc, Tessellator.instance, dX, dY, dZ);
                }
            }
        }

        glPopMatrix();

        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
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
                    paintbrush.setDamage(stack, paintbrush.getDamage(paintbrush.getColorFromDamage(stack), paintbrush.getInkFromDamage(stack), size, paintbrush.isStackInfinite(stack)));
                    event.setCanceled(true);
                }
            }
        }
    }
}
