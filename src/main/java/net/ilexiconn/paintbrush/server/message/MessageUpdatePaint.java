package net.ilexiconn.paintbrush.server.message;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.server.util.PaintedBlock;
import net.minecraft.entity.player.EntityPlayer;

public class MessageUpdatePaint extends AbstractMessage<MessageUpdatePaint> {
    public PaintedBlock paintedBlock;

    public MessageUpdatePaint() {

    }

    public MessageUpdatePaint(PaintedBlock paintedBlock) {
        this.paintedBlock = paintedBlock;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientMessage(MessageUpdatePaint message, EntityPlayer player) {

    }

    @Override
    public void handleServerMessage(MessageUpdatePaint message, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PaintedBlock paintedBlock = new PaintedBlock();
        paintedBlock.decode(buf);
        this.paintedBlock = paintedBlock;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        paintedBlock.encode(buf);
    }
}
