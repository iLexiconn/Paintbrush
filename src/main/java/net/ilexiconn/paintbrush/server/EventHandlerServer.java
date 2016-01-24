package net.ilexiconn.paintbrush.server;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.MessageUpdateData;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.PaintedBlock;
import net.ilexiconn.paintbrush.server.util.PaintedFace;
import net.ilexiconn.paintbrush.server.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.List;

public class EventHandlerServer {
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.world.isRemote) {
            PaintbrushDataServer.reset();
            PaintbrushDataServer.get(event.world);
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
            PaintbrushDataServer data = PaintbrushDataServer.get(event.world);
            if (data != null) {
                for (PaintedBlock paintedBlock : data.paintedBlockList) {
                    Paintbrush.networkWrapper.sendTo(new MessageUpdateData(Utils.BLOCK, paintedBlock, false), (EntityPlayerMP) event.entity);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (!event.world.isRemote) {
            PaintbrushDataServer data = PaintbrushDataServer.get(event.world);
            if (data != null) {
                BlockPos pos = new BlockPos(event.x, event.y, event.z);
                List<PaintedFace> toRemove = Lists.newArrayList();
                for (PaintedBlock paintedBlock : data.paintedBlockList) {
                    for (PaintedFace paintedFace : paintedBlock.paintedFaceList) {
                        if (paintedBlock.pos.equals(pos.offset(0, -1, 0)) && paintedFace.facing == EnumFacing.UP) {
                            toRemove.add(paintedFace);
                        } else if (paintedBlock.pos.equals(pos.offset(0, 1, 0)) && paintedFace.facing == EnumFacing.DOWN) {
                            toRemove.add(paintedFace);
                        } else if (paintedBlock.pos.equals(pos.offset(-1, 0, 0)) && paintedFace.facing == EnumFacing.WEST) {
                            toRemove.add(paintedFace);
                        } else if (paintedBlock.pos.equals(pos.offset(1, 0, 0)) && paintedFace.facing == EnumFacing.EAST) {
                            toRemove.add(paintedFace);
                        } else if (paintedBlock.pos.equals(pos.offset(0, 0, 1)) && paintedFace.facing == EnumFacing.NORTH) {
                            toRemove.add(paintedFace);
                        } else if (paintedBlock.pos.equals(pos.offset(0, 0, -1)) && paintedFace.facing == EnumFacing.SOUTH) {
                            toRemove.add(paintedFace);
                        }
                    }
                }
                for (PaintedFace paintedFace : toRemove) {
                    data.removePaintedFace(paintedFace);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.world.isRemote) {
            PaintbrushDataServer data = PaintbrushDataServer.get(event.world);
            if (data != null) {
                BlockPos pos = new BlockPos(event.x, event.y, event.z);
                List<PaintedBlock> toRemove = Lists.newArrayList();
                for (PaintedBlock paintedBlock : data.paintedBlockList) {
                    if (paintedBlock.pos.equals(pos)) {
                        toRemove.add(paintedBlock);
                    }
                }
                for (PaintedBlock paintedBlock : toRemove) {
                    data.removePaintedBlock(paintedBlock);
                }
            }
        }
    }
}
