package net.ilexiconn.paintbrush.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraftforge.common.MinecraftForge;

public class ProxyServer {
    public void onInit() {
        EventHandlerServer eventHandler = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
        FMLInterModComms.sendMessage("llibrary", "update-checker", "http://pastebin.com/raw/r7uSy5Ti");
    }
}
