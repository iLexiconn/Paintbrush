package net.ilexiconn.paintbrush.server.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.common.message.AbstractMessage;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RemovePaintMessage extends AbstractMessage<RemovePaintMessage> {
    private int entityID;
    private Paint paint;

    public RemovePaintMessage() {

    }

    public RemovePaintMessage(PaintedBlockEntity entity, Paint paint) {
        this.entityID = entity.getEntityId();
        this.paint = paint;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientMessage(RemovePaintMessage message, EntityPlayer player) {
        Entity entity = player.worldObj.getEntityByID(message.entityID);
        if (entity != null && entity instanceof PaintedBlockEntity) {
            PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;
            paintedBlockEntity.paintList.remove(message.paint);
        }
    }

    @Override
    public void handleServerMessage(RemovePaintMessage message, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.paint = Paint.decode(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityID);
        this.paint.encode(buf);
    }
}
