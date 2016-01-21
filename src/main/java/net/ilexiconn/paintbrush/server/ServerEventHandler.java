package net.ilexiconn.paintbrush.server;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.ilexiconn.paintbrush.server.world.PaintbrushData;
import net.minecraftforge.event.world.WorldEvent;

public class ServerEventHandler {
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.world.isRemote) {
            PaintbrushData.reset();
            PaintbrushData.get(event.world);
        }
    }
}
