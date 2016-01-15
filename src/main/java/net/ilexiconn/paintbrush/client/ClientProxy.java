package net.ilexiconn.paintbrush.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.ServerProxy;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
    public void init() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }
}
