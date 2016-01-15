package net.ilexiconn.paintbrush.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.ilexiconn.paintbrush.server.world.PaintbrushData;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldEvent;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldEvent.Post event) {
        PaintbrushData data = PaintbrushData.get(event.renderer.worldObj);
        Tessellator tessellator = Tessellator.instance;
        for (Paint paint : data.getPaint()) {
            tessellator.startDrawingQuads();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_CULL_FACE);
            switch (paint.getFacing()) {
                case NORTH:
                    tessellator.addVertex(paint.getPos().getX() - paint.getX() * 0.0625F, paint.getPos().getY() - paint.getY() * 0.0625F, paint.getPos().getZ());
                    tessellator.addVertex(paint.getPos().getX() + 0.0625F - paint.getX() * 0.0625F, paint.getPos().getY() - paint.getY() * 0.0625F, paint.getPos().getZ());
                    tessellator.addVertex(paint.getPos().getX() + 0.0625F - paint.getX() * 0.0625F, paint.getPos().getY() + 0.0625F - paint.getY() * 0.0625F, paint.getPos().getZ());
                    tessellator.addVertex(paint.getPos().getX() - paint.getX() * 0.0625F, paint.getPos().getY() + 0.0625F - paint.getY() * 0.0625F, paint.getPos().getZ());
                    break;
            }
            tessellator.draw();
        }
    }
}
