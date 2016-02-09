package net.ilexiconn.paintbrush.server;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class ProxyServer {
    public void onInit() {
        EventHandlerServer eventHandler = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLInterModComms.sendMessage("llibrary", "update-checker", "http://pastebin.com/raw/r7uSy5Ti");
    }

    public void onPostInit() {

    }
}
