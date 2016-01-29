package net.ilexiconn.paintbrush.server.message;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.server.item.EraserItem;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static net.ilexiconn.paintbrush.server.item.PaintbrushItem.*;

public class UpdateSizeMessage extends AbstractMessage<UpdateSizeMessage> {
    private int size;

    public UpdateSizeMessage() {

    }

    public UpdateSizeMessage(int size) {
        this.size = size;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientMessage(UpdateSizeMessage message, EntityPlayer player) {

    }

    @Override
    public void handleServerMessage(UpdateSizeMessage message, EntityPlayer player) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof PaintbrushItem) {
            stack.setItemDamage(getDamage(getColorFromDamage(stack), getInkFromDamage(stack), message.size, isStackInfinite(stack)));
        } else if (stack != null && stack.getItem() instanceof EraserItem) {
            stack.setItemDamage(message.size);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        size = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(size);
    }
}
