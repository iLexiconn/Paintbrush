package net.ilexiconn.paintbrush.server.message;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class UpdatePaintedBlockMessage extends AbstractMessage<UpdatePaintedBlockMessage> {
    private int entityID;
    private List<Paint> paintList;

    public UpdatePaintedBlockMessage() {

    }

    public UpdatePaintedBlockMessage(PaintedBlockEntity entity, List<Paint> paintList) {
        this.entityID = entity.getEntityId();
        this.paintList = paintList;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientMessage(UpdatePaintedBlockMessage message, EntityPlayer player) {
        Entity entity = player.worldObj.getEntityByID(message.entityID);
        if (entity != null && entity instanceof PaintedBlockEntity) {
            System.out.println("Received packet on CLIENT size with size " + message.paintList.size());
            PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;
            paintedBlockEntity.paintList = message.paintList;
        }
    }

    @Override
    public void handleServerMessage(UpdatePaintedBlockMessage message, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.paintList = Lists.newArrayList();
        int paintListSize = buf.readInt();
        for (int i = 0; i < paintListSize; i++) {
            Paint paint = Paint.decode(buf);
            this.paintList.add(paint);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeInt(this.paintList.size());
        for (Paint paint : this.paintList) {
            paint.encode(buf);
        }
    }
}
