package net.ilexiconn.paintbrush.server.message;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.server.util.Util;
import net.ilexiconn.paintbrush.server.util.Utils;
import net.minecraft.entity.player.EntityPlayer;

public class MessageUpdateData extends AbstractMessage<MessageUpdateData> {
    private Utils type;
    private Util util;

    public MessageUpdateData() {

    }

    public MessageUpdateData(Utils type, Util util) {
        this.type = type;
        this.util = util;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientMessage(MessageUpdateData message, EntityPlayer player) {
        System.out.println("Received " + message.util);
    }

    @Override
    public void handleServerMessage(MessageUpdateData message, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Utils.values()[buf.readByte()];
        if (type != null) {
            try {
                util = type.getUtilClass().newInstance();
                util = util.decode(buf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        util.encode(buf);
    }
}
