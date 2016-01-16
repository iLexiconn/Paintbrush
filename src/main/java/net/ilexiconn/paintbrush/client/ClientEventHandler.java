package net.ilexiconn.paintbrush.client;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    private static List<Paint> paintList = Lists.newArrayList();

    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        Tessellator tessellator = Tessellator.instance;
        for (Paint paint : paintList) {
            System.out.println("Rendering " + paint.getColor());
            tessellator.startDrawingQuads();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_CULL_FACE);
            tessellator.addVertex(paint.getPos().getX(), paint.getPos().getY(), paint.getPos().getZ());
            tessellator.addVertex(paint.getPos().getX() + 1, paint.getPos().getY(), paint.getPos().getZ());
            tessellator.addVertex(paint.getPos().getX() + 1, paint.getPos().getY() + 1, paint.getPos().getZ());
            tessellator.addVertex(paint.getPos().getX(), paint.getPos().getY() + 1, paint.getPos().getZ());
            tessellator.draw();
        }
    }

    public static void addPaint(Paint paint) {
        System.out.println("Adding paint on CLIENT");
        paintList.add(paint);
    }
}
