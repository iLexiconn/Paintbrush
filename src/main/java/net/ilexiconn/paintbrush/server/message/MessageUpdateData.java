package net.ilexiconn.paintbrush.server.message;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.server.util.Util;
import net.ilexiconn.paintbrush.server.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class MessageUpdateData extends AbstractMessage<MessageUpdateData> {
    private Utils type;
    private Util util;
    private boolean flag;

    public MessageUpdateData() {

    }

    public MessageUpdateData(Utils type, Util util, boolean flag) {
        this.type = type;
        this.util = util;
        this.flag = flag;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientMessage(MessageUpdateData message, EntityPlayer player) {
        if (message.util != null) {
            message.util.updateClient(Minecraft.getMinecraft(), message.flag);
        }
    }

    @Override
    public void handleServerMessage(MessageUpdateData message, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Utils.values()[buf.readByte()];
        flag = buf.readBoolean();
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
        buf.writeBoolean(flag);
        util.encode(buf);
    }
}
