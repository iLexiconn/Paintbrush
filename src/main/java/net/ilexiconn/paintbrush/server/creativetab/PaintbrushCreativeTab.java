package net.ilexiconn.paintbrush.server.creativetab;

import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PaintbrushCreativeTab extends CreativeTabs {
    public PaintbrushCreativeTab() {
        super("paintbrush");
    }

    @Override
    public Item getTabIconItem() {
        return Paintbrush.paintbrush;
    }

    @SideOnly(Side.CLIENT)
    public int getIconItemDamage() {
        return PaintbrushItem.getDamage(EnumChatFormatting.YELLOW.getColorIndex(), 0);
    }
}
