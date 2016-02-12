package net.ilexiconn.paintbrush;

import net.ilexiconn.llibrary.common.message.AbstractMessage;
import net.ilexiconn.paintbrush.server.ProxyServer;
import net.ilexiconn.paintbrush.server.api.PaintbrushAPI;
import net.ilexiconn.paintbrush.server.creativetab.PaintbrushCreativeTab;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.item.PaintScraperItem;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.message.AddPaintMessage;
import net.ilexiconn.paintbrush.server.message.RemovePaintMessage;
import net.ilexiconn.paintbrush.server.message.UpdateSizeMessage;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "paintbrush", name = "Paintbrush", version = Paintbrush.VERSION, dependencies = "required-after:llibrary@[" + Paintbrush.LLIBRARY_VERSION + ",)")
public class Paintbrush {
    public static final String VERSION = "0.1.1-develop";
    public static final String LLIBRARY_VERSION = "0.7.0";
    @SidedProxy(serverSide = "net.ilexiconn.paintbrush.server.ProxyServer", clientSide = "net.ilexiconn.paintbrush.client.ProxyClient")
    public static ProxyServer proxy;
    public static SimpleNetworkWrapper networkWrapper;

    public static PaintbrushCreativeTab creativeTab;
    public static PaintbrushItem paintbrush;
    public static PaintScraperItem paintScraper;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("paintbrush");
        AbstractMessage.registerMessage(networkWrapper, AddPaintMessage.class, 0, Side.CLIENT);
        AbstractMessage.registerMessage(networkWrapper, UpdateSizeMessage.class, 1, Side.SERVER);
        AbstractMessage.registerMessage(networkWrapper, RemovePaintMessage.class, 2, Side.CLIENT);

        creativeTab = new PaintbrushCreativeTab();
        paintbrush = new PaintbrushItem();
        paintScraper = new PaintScraperItem();

        EntityRegistry.registerModEntity(PaintedBlockEntity.class, "paintedBlock", 0, this, 64, 1, true);
        GameRegistry.registerItem(paintbrush, "paintbrush");
        GameRegistry.registerItem(paintScraper, "paint_scraper");

        PaintbrushAPI.registerIgnoredBlockType(IPlantable.class);

        proxy.onInit();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        proxy.onPostInit();
    }

    @Mod.EventHandler
    public void onIMCReceived(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.key.equalsIgnoreCase("ignore")) {
                if (message.isItemStackMessage()) {
                    ItemStack stack = message.getItemStackValue();
                    Item item = stack.getItem();
                    Block block = Block.getBlockFromItem(item);
                    if (block != null) {
                        PaintbrushAPI.registerIgnoredBlock(block);
                    }
                } else if (message.isStringMessage()) {
                    String string = message.getStringValue();
                    try {
                        Class<?> type = Class.forName(string);
                        PaintbrushAPI.registerIgnoredBlockType(type);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }
        }
    }
}
