package net.ilexiconn.paintbrush.server.entity;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.AddPaintMessage;
import net.ilexiconn.paintbrush.server.message.RemovePaintMessage;
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
    public int blockX;
    public int blockY;
    public int blockZ;

    public PaintedBlockEntity(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
    }

    public boolean addPaint(Paint paint) {
        for (Paint p : this.paintList) {
            if (p.posX == paint.posX && p.posY == paint.posY && p.facing == paint.facing) {
                return false;
            }
        }
        this.paintList.add(paint);
        Paintbrush.networkWrapper.sendToAll(new AddPaintMessage(this, paint));
        return true;
    }

    public void removePaint(int x, int y, EnumFacing facing) {
        Paint toRemove = null;
        for (Paint paint : this.paintList) {
            if (paint.posX == x && paint.posY == y && paint.facing == facing) {
                toRemove = paint;
                break;
            }
        }
        if (toRemove != null) {
            this.paintList.remove(toRemove);
            Paintbrush.networkWrapper.sendToAll(new RemovePaintMessage(this, toRemove));
        }
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForFace(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return this.worldObj.getLightBrightnessForSkyBlocks(blockX, blockY, blockZ - 1, 0);
            case SOUTH:
                return this.worldObj.getLightBrightnessForSkyBlocks(blockX, blockY, blockZ + 1, 0);
            case EAST:
                return this.worldObj.getLightBrightnessForSkyBlocks(blockX - 1, blockY, blockZ, 0);
            case WEST:
                return this.worldObj.getLightBrightnessForSkyBlocks(blockX + 1, blockY, blockZ, 0);
            case UP:
                return this.worldObj.getLightBrightnessForSkyBlocks(blockX, blockY + 1, blockZ, 0);
            case DOWN:
                return this.worldObj.getLightBrightnessForSkyBlocks(blockX, blockY - 1, blockZ, 0);
            default:
                return 0;
        }
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void onUpdate() {
        if (this.worldObj.isAirBlock(blockX, blockY, blockZ)) {
            this.setDead();
        } else if (this.paintList.isEmpty()) {
            this.setDead();
        }
        List<Paint> toRemove = Lists.newArrayList();
        for (Paint paint : paintList) {
            if (!worldObj.isAirBlock(blockX, blockY + 1, blockZ) && paint.facing == EnumFacing.UP) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockX, blockY - 1, blockZ) && paint.facing == EnumFacing.DOWN) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockX + 1, blockY, blockZ) && paint.facing == EnumFacing.WEST) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockX - 1, blockY, blockZ) && paint.facing == EnumFacing.EAST) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockX, blockY, blockZ - 1) && paint.facing == EnumFacing.NORTH) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockX, blockY, blockZ + 1) && paint.facing == EnumFacing.SOUTH) {
                toRemove.add(paint);
            }
        }
        this.paintList.removeAll(toRemove);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.blockX = compound.getInteger("BlockX");
        this.blockY = compound.getInteger("BlockY");
        this.blockZ = compound.getInteger("BlockZ");
        this.paintList = Lists.newArrayList();
        int size = compound.getInteger("Size");
        NBTTagList list = compound.getTagList("Paint", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < size; i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            Paint paint = Paint.readFromNBT(tag);
            this.paintList.add(paint);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("BlockX", this.blockX);
        compound.setInteger("BlockY", this.blockY);
        compound.setInteger("BlockZ", this.blockZ);
        compound.setInteger("Size", this.paintList.size());
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
        buf.writeInt(this.blockX);
        buf.writeInt(this.blockY);
        buf.writeInt(this.blockZ);
        buf.writeInt(this.paintList.size());
        for (Paint paint : this.paintList) {
            paint.encode(buf);
        }
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.blockX = buf.readInt();
        this.blockY = buf.readInt();
        this.blockZ = buf.readInt();
        this.paintList = Lists.newArrayList();
        int paintListSize = buf.readInt();
        for (int i = 0; i < paintListSize; i++) {
            Paint paint = Paint.decode(buf);
            this.paintList.add(paint);
        }
    }
}
