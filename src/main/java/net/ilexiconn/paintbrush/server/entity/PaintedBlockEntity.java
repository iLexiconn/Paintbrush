package net.ilexiconn.paintbrush.server.entity;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.AddPaintMessage;
import net.ilexiconn.paintbrush.server.message.UpdatePaintedBlockMessage;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class PaintedBlockEntity extends Entity {
    public List<Paint> paintList = Lists.newArrayList();

    public PaintedBlockEntity(World world) {
        super(world);
    }

    public void addPaint(Paint paint) {
        if (this.paintList.contains(paint)) {
            return;
        }
        this.paintList.add(paint);
        Paintbrush.networkWrapper.sendToAll(new AddPaintMessage(this, paint));
    }

    @Override
    protected void entityInit() {
        System.out.println("Initializing entity on " + FMLCommonHandler.instance().getEffectiveSide() + " size.");
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        System.out.println("Reading entity on " + FMLCommonHandler.instance().getEffectiveSide() + " size.");
        this.paintList = Lists.newArrayList();
        int size = compound.getInteger("Size");
        NBTTagList list = compound.getTagList("Size", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < size; i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            Paint paint = Paint.readFromNBT(tag);
            this.paintList.add(paint);
        }
        Paintbrush.networkWrapper.sendToAll(new UpdatePaintedBlockMessage(this, this.paintList));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        System.out.println("Writing entity on " + FMLCommonHandler.instance().getEffectiveSide() + " size.");
        compound.setInteger("Size", paintList.size());
        NBTTagList list = new NBTTagList();
        for (Paint paint : this.paintList) {
            NBTTagCompound tag = new NBTTagCompound();
            paint.writeToNBT(tag);
            list.appendTag(tag);
        }
        compound.setTag("Paint", list);
    }
}
