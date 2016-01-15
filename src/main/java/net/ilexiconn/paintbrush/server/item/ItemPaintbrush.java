package net.ilexiconn.paintbrush.server.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.util.BlockPos;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.ilexiconn.paintbrush.server.world.PaintbrushData;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemPaintbrush extends Item {
    @SideOnly(Side.CLIENT)
    private IIcon colorOverlay;

    public ItemPaintbrush() {
        setUnlocalizedName("paintbrush");
        setCreativeTab(CreativeTabs.tabTools);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
//        if (renderPass != 0) {
//            int damage = stack.getItemDamage();
//            EnumChatFormatting color = EnumChatFormatting.values()[damage];
//            return color.
//        }

        return 0xFFFFFF;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            PaintbrushData data = PaintbrushData.get(world);
            int damage = stack.getItemDamage();
            EnumChatFormatting color = EnumChatFormatting.values()[damage];
            Paint paint = new Paint(color, itemRand.nextInt(15), itemRand.nextInt(15), EnumFacing.values()[face], new BlockPos(x, y, z));
            data.addPaint(paint);
        }

        return true;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips) {
        int damage = stack.getItemDamage();
        EnumChatFormatting color = EnumChatFormatting.values()[damage];

        info.add(color + StatCollector.translateToLocal("dye." + color.getFriendlyName() + ".name"));
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        for (int dyeType = 0; dyeType < 16; dyeType++) {
            items.add(new ItemStack(item, 1, dyeType));
        }
    }
}
