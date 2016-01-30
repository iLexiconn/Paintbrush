package net.ilexiconn.paintbrush.server.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class PaintScraperItem extends Item {
    public PaintScraperItem() {
        setUnlocalizedName("paint_scraper");
        setCreativeTab(CreativeTabs.tabTools);
        setTextureName("paintbrush:paint_scraper");
        setMaxStackSize(1);
        setMaxDamage(64);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        PaintedBlockEntity paintedBlock = null;
        for (Object entity : world.loadedEntityList) {
            if (entity instanceof PaintedBlockEntity) {
                PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;
                if (paintedBlockEntity.blockX == x && paintedBlockEntity.blockY == y && paintedBlockEntity.blockZ == z) {
                    paintedBlock = paintedBlockEntity;
                    break;
                }
            }
        }
        if (paintedBlock != null) {
            int pixelX = (int) (hitX * 16);
            int pixelY = (int) (hitY * 16);
            int pixelZ = (int) (hitZ * 16);

            int offsetX = 0;
            int offsetY = 0;

            EnumFacing facing = EnumFacing.values()[face];

            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                offsetX = pixelX;
                offsetY = pixelZ;
            } else if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
                offsetX = pixelX;
                offsetY = pixelY;
            } else if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
                offsetX = pixelZ;
                offsetY = pixelY;
            }

            paintedBlock.removePaint(offsetX, offsetY, facing);

            for (int ring = 0; ring < stack.getItemDamage(); ring++) {
                for (int i = 0; i < 360; ++i) {
                    double rad = Math.toRadians((double) i);
                    int pX = (int) (-Math.sin(rad) * ring);
                    int pY = (int) (Math.cos(rad) * ring);
                    paintedBlock.removePaint(offsetX + pX, offsetY + pY, facing);
                }
            }
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean advancedTooltips) {
        info.add(StatCollector.translateToLocal("tooltip.paintbrush.size") + ": " + stack.getItemDamage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List items) {
        items.add(new ItemStack(item, 1, 1));
    }
}
