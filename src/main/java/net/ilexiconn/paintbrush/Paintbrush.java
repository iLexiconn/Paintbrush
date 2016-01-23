package net.ilexiconn.paintbrush;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.ilexiconn.paintbrush.server.ProxyServer;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.message.MessageUpdateData;

@Mod(modid = "paintbrush", name = "Paintbrush", version = Paintbrush.VERSION)
public class Paintbrush {
    @SidedProxy(serverSide = "net.ilexiconn.paintbrush.server.ProxyServer", clientSide = "net.ilexiconn.paintbrush.client.ProxyClient")
    public static ProxyServer proxy;
    public static SimpleNetworkWrapper networkWrapper;

    public static final String VERSION = "0.1.0";

    /*
     * TODO
     *
     * - Paintbrush sizes
     * - Paintbrush ink and recipes (dye)
     * - Fix crash when spamming paint on 'new' blocks
     * - Support for multiple dimensions
     * - Paint lighting
     */

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("paintbrush");
        networkWrapper.registerMessage(MessageUpdateData.class, MessageUpdateData.class, 0, Side.CLIENT);

        GameRegistry.registerItem(new PaintbrushItem(), "paintbrush");
        proxy.init();
    }
}
