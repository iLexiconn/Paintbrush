package net.ilexiconn.paintbrush.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.client.render.PaintedBlockRenderer;
import net.ilexiconn.paintbrush.server.ProxyServer;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyServer {
    public void onInit() {
        super.onInit();

        EventHandlerClient eventHandler = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
        RenderingRegistry.registerEntityRenderingHandler(PaintedBlockEntity.class, new PaintedBlockRenderer());
    }
}
