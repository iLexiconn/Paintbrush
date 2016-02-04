package net.ilexiconn.paintbrush.client;

import net.ilexiconn.paintbrush.client.render.PaintedBlockRenderer;
import net.ilexiconn.paintbrush.server.ProxyServer;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyServer {
    public void init() {
        super.init();

        EventHandlerClient eventHandler = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        RenderingRegistry.registerEntityRenderingHandler(PaintedBlockEntity.class, new PaintedBlockRenderer(Minecraft.getMinecraft().getRenderManager()));
    }
}
