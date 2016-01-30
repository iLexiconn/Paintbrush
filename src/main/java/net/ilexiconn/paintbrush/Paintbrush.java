package net.ilexiconn.paintbrush;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.ilexiconn.paintbrush.server.ProxyServer;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.item.PaintScraperItem;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.ilexiconn.paintbrush.server.message.AbstractMessage;
import net.ilexiconn.paintbrush.server.message.AddPaintMessage;
import net.ilexiconn.paintbrush.server.message.UpdateSizeMessage;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

@Mod(modid = "paintbrush", name = "Paintbrush", version = Paintbrush.VERSION)
public class Paintbrush {
    @SidedProxy(serverSide = "net.ilexiconn.paintbrush.server.ProxyServer", clientSide = "net.ilexiconn.paintbrush.client.ProxyClient")
    public static ProxyServer proxy;
    public static SimpleNetworkWrapper networkWrapper;

    public static final String VERSION = "0.1.0";

    public static PaintbrushItem paintbrush;

    public static final String[] nameToID = new String[] {"black", "red", "dark_green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "green", "yellow", "light_blue", "magenta", "gold", "white"};

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("paintbrush");
        AbstractMessage.registerMessage(networkWrapper, AddPaintMessage.class, 0, Side.CLIENT);
        AbstractMessage.registerMessage(networkWrapper, UpdateSizeMessage.class, 1, Side.SERVER);

        paintbrush = new PaintbrushItem();

        EntityRegistry.registerModEntity(PaintedBlockEntity.class, "paintedBlock", 0, this, 64, 1, true);
        GameRegistry.registerItem(paintbrush, "paintbrush");
        GameRegistry.registerItem(new PaintScraperItem(), "paint_scraper");

        for (int color = 0; color < 16; color++) {
            EnumChatFormatting chatFormatting = EnumChatFormatting.values()[color];

            int dyeColorIndex = 0;

            for (dyeColorIndex = 0; dyeColorIndex < 16; dyeColorIndex++) {
                if (nameToID[dyeColorIndex].equalsIgnoreCase(chatFormatting.getFriendlyName())) {
                    break;
                }
            }

            GameRegistry.addShapedRecipe(new ItemStack(paintbrush, 1, PaintbrushItem.getDamage(color, 64, 1, false)), "WDW", "SPS", " S ", 'P', Blocks.planks, 'S', Items.stick, 'D', new ItemStack(Items.dye, 1, dyeColorIndex), 'W', Blocks.wool);
        }

        proxy.init();
    }
}
