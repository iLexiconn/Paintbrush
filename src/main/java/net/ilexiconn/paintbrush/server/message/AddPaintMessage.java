package net.ilexiconn.paintbrush.server.message;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AddPaintMessage extends AbstractMessage<AddPaintMessage> {
    private int entityID;
    private Paint paint;

    public AddPaintMessage() {

    }

    public AddPaintMessage(PaintedBlockEntity entity, Paint paint) {
        this.entityID = entity.getEntityId();
        this.paint = paint;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientMessage(AddPaintMessage message, EntityPlayer player) {
        Entity entity = player.worldObj.getEntityByID(message.entityID);
        if (entity != null && entity instanceof PaintedBlockEntity) {
            PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;
            paintedBlockEntity.paintList.add(message.paint);
        }
    }

    @Override
    public void handleServerMessage(AddPaintMessage message, EntityPlayer player) {

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
