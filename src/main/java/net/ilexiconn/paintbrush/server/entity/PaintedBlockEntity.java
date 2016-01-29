package net.ilexiconn.paintbrush.server.entity;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.AddPaintMessage;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class PaintedBlockEntity extends Entity implements IEntityAdditionalSpawnData {
    public List<Paint> paintList = Lists.newArrayList();

    public PaintedBlockEntity(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
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

    }

    @Override
    public void onUpdate() {
        if (this.worldObj.isAirBlock((int) posX, (int) posY, (int) posZ)) {
            this.setDead();
        }
        List<Paint> toRemove = Lists.newArrayList();
        for (Paint paint : paintList) {
            if (!worldObj.isAirBlock((int) this.posX, (int) this.posY + 1, (int) this.posZ) && paint.facing == EnumFacing.UP) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock((int) this.posX, (int) this.posY - 1, (int) this.posZ) && paint.facing == EnumFacing.DOWN) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock((int) this.posX + 1, (int) this.posY, (int) this.posZ) && paint.facing == EnumFacing.WEST) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock((int) this.posX - 1, (int) this.posY, (int) this.posZ) && paint.facing == EnumFacing.EAST) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock((int) this.posX, (int) this.posY, (int) this.posZ - 1) && paint.facing == EnumFacing.NORTH) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock((int) this.posX, (int) this.posY, (int) this.posZ + 1) && paint.facing == EnumFacing.SOUTH) {
                toRemove.add(paint);
            }
        }
        for (Paint paint : toRemove) {
            this.paintList.remove(paint);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.paintList = Lists.newArrayList();
        int size = compound.getInteger("Size");
        NBTTagList list = compound.getTagList("Size", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < size; i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            Paint paint = Paint.readFromNBT(tag);
            this.paintList.add(paint);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("Size", paintList.size());
        NBTTagList list = new NBTTagList();
        for (Paint paint : this.paintList) {
            NBTTagCompound tag = new NBTTagCompound();
            paint.writeToNBT(tag);
            list.appendTag(tag);
        }
        compound.setTag("Paint", list);
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int p_70056_9_) {

    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        System.out.println("Writing spawn data on " + FMLCommonHandler.instance().getEffectiveSide() + " side");
        buf.writeInt(this.paintList.size());
        for (Paint paint : this.paintList) {
            paint.encode(buf);
        }
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        System.out.println("Reading spawn data on " + FMLCommonHandler.instance().getEffectiveSide() + " side");
        this.paintList = Lists.newArrayList();
        int paintListSize = buf.readInt();
        for (int i = 0; i < paintListSize; i++) {
            Paint paint = Paint.decode(buf);
            this.paintList.add(paint);
        }
    }
}
