package net.ilexiconn.paintbrush;

import net.ilexiconn.llibrary.common.message.AbstractMessage;
import net.ilexiconn.paintbrush.server.ProxyServer;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.item.PaintScraperItem;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.message.AddPaintMessage;
import net.ilexiconn.paintbrush.server.message.RemovePaintMessage;
import net.ilexiconn.paintbrush.server.message.UpdateSizeMessage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "paintbrush", name = "Paintbrush", version = Paintbrush.VERSION)
public class Paintbrush {
    @SidedProxy(serverSide = "net.ilexiconn.paintbrush.server.ProxyServer", clientSide = "net.ilexiconn.paintbrush.client.ProxyClient")
    public static ProxyServer proxy;
    public static SimpleNetworkWrapper networkWrapper;

    public static final String VERSION = "0.1.0";

    public static PaintbrushItem paintbrush;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("paintbrush");
        AbstractMessage.registerMessage(networkWrapper, AddPaintMessage.class, 0, Side.CLIENT);
        AbstractMessage.registerMessage(networkWrapper, UpdateSizeMessage.class, 1, Side.SERVER);
        AbstractMessage.registerMessage(networkWrapper, RemovePaintMessage.class, 2, Side.CLIENT);

        paintbrush = new PaintbrushItem();

        EntityRegistry.registerModEntity(PaintedBlockEntity.class, "paintedBlock", 0, this, 64, 1, true);
        GameRegistry.registerItem(paintbrush, "paintbrush");
        GameRegistry.registerItem(new PaintScraperItem(), "paint_scraper");

        proxy.init();
    }
}
