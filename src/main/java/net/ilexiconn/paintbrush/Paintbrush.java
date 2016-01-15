package net.ilexiconn.paintbrush;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.ilexiconn.paintbrush.server.ServerProxy;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;

@Mod(modid = "paintbrush", name = "Paintbrush", version = "0.1.0")
public class Paintbrush {
    @SidedProxy(serverSide = "net.ilexiconn.paintbrush.server.ServerProxy", clientSide = "net.ilexiconn.paintbrush.client.ClientProxy")
    public static ServerProxy proxy;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerItem(new PaintbrushItem(), "paintbrush");
        proxy.init();
    }
}
