package net.ilexiconn.paintbrush;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.ilexiconn.paintbrush.server.CommonEventHandler;
import net.ilexiconn.paintbrush.server.ServerProxy;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.message.MessageUpdatePaint;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "paintbrush", name = "Paintbrush", version = "0.1.0")
public class Paintbrush {
    @SidedProxy(serverSide = "net.ilexiconn.paintbrush.server.ServerProxy", clientSide = "net.ilexiconn.paintbrush.client.ClientProxy")
    public static ServerProxy proxy;
    public static SimpleNetworkWrapper networkWrapper;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("paintbrush");
        networkWrapper.registerMessage(MessageUpdatePaint.class, MessageUpdatePaint.class, 0, Side.CLIENT);

        GameRegistry.registerItem(new PaintbrushItem(), "paintbrush");
        proxy.init();

        CommonEventHandler eventHandler = new CommonEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(event);
    }
}
