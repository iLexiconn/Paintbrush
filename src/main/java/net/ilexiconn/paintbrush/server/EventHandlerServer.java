package net.ilexiconn.paintbrush.server;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.MessageUpdateData;
import net.ilexiconn.paintbrush.server.util.PaintedBlock;
import net.ilexiconn.paintbrush.server.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

public class EventHandlerServer {
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.world.isRemote) {
            PaintbrushDataServer.reset();
            PaintbrushDataServer.get(event.world);
        }
    }

    @SubscribeEvent
    public void onPlayerjoin(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
            PaintbrushDataServer data = PaintbrushDataServer.get(event.world);
            if (data != null) {
                for (PaintedBlock paintedBlock : data.getPaintedBlocks()) {
                    Paintbrush.networkWrapper.sendTo(new MessageUpdateData(Utils.BLOCK, paintedBlock, false), (EntityPlayerMP) event.entity);
                }
            }
        }
    }
}
