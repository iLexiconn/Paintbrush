package net.ilexiconn.paintbrush.server;

import net.minecraftforge.common.MinecraftForge;

public class ProxyServer {
    public void init() {
        EventHandlerServer eventHandler = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }
}
