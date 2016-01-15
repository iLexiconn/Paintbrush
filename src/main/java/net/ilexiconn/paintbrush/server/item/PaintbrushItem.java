package net.ilexiconn.paintbrush.server.item;

import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.ilexiconn.paintbrush.server.world.PaintbrushData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class PaintbrushItem extends Item {
    public PaintbrushItem() {
        setUnlocalizedName("paintbrush");
        setCreativeTab(CreativeTabs.tabTools);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        PaintbrushData data = PaintbrushData.get(world);
        Paint paint = new Paint(EnumChatFormatting.BLACK, itemRand.nextInt(15), itemRand.nextInt(15), EnumFacing.values()[face], new BlockPos(x, y, z));
        data.addPaint(paint);
        return false;
    }
}
