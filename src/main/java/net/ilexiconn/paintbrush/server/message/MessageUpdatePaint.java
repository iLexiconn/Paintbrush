package net.ilexiconn.paintbrush.server.message;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.client.ClientEventHandler;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;

public class MessageUpdatePaint extends AbstractMessage<MessageUpdatePaint> {
    public Paint paint;

    public MessageUpdatePaint() {

    }

    public MessageUpdatePaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientMessage(MessageUpdatePaint message, EntityPlayer player) {
        ClientEventHandler.addPaint(message.paint);
    }

    @Override
    public void handleServerMessage(MessageUpdatePaint message, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        paint = new Paint(EnumChatFormatting.values()[buf.readInt()], buf.readInt(), buf.readInt(), EnumFacing.values()[buf.readInt()], new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(paint.getColor().ordinal());
        buf.writeInt(paint.getX());
        buf.writeInt(paint.getY());
        buf.writeInt(paint.getFacing().ordinal());
        buf.writeInt(paint.getPos().getX());
        buf.writeInt(paint.getPos().getY());
        buf.writeInt(paint.getPos().getZ());
    }
}
