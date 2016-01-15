package net.ilexiconn.paintbrush.server;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.ilexiconn.paintbrush.server.world.PaintbrushData;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class CommonEventHandler
{
    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event) {
        World world = event.world;

        if (!world.isRemote) {
            PaintbrushData.instance = null;
            PaintbrushData.get(world);
        }
    }
}
