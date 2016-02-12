package net.ilexiconn.paintbrush.server.creativetab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.item.PaintbrushItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

public class PaintbrushCreativeTab extends CreativeTabs {
    public PaintbrushCreativeTab() {
        super("paintbrush");
    }

    @Override
    public Item getTabIconItem() {
        return Paintbrush.paintbrush;
    }

    @SideOnly(Side.CLIENT)
    public int func_151243_f() {
        return PaintbrushItem.getDamage(EnumChatFormatting.YELLOW.ordinal(), 0);
    }
}
