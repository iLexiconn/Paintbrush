package net.ilexiconn.paintbrush.server;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy {
    public void init() {
        CommonEventHandler eventHandler = new CommonEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
    }

    public EntityPlayer getClientPlayer() {
        return null;
    }
}
