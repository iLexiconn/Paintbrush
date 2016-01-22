package net.ilexiconn.paintbrush.server;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

public class EventHandlerServer {
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.world.isRemote) {
            PaintbrushDataServer.reset();
            PaintbrushDataServer.get(event.world);
        }
    }
}
