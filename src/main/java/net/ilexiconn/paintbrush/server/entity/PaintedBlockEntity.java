package net.ilexiconn.paintbrush.server.entity;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.server.message.AddPaintMessage;
import net.ilexiconn.paintbrush.server.message.RemovePaintMessage;
import net.ilexiconn.paintbrush.server.util.Paint;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PaintedBlockEntity extends Entity implements IEntityAdditionalSpawnData {
    public List<Paint> paintList = Lists.newArrayList();
    public BlockPos blockPos;

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
                return this.worldObj.getCombinedLight(blockPos.north(), 0);
            case SOUTH:
                return this.worldObj.getCombinedLight(blockPos.south(), 0);
            case EAST:
                return this.worldObj.getCombinedLight(blockPos.east(), 0);
            case WEST:
                return this.worldObj.getCombinedLight(blockPos.west(), 0);
            case UP:
                return this.worldObj.getCombinedLight(blockPos.up(), 0);
            case DOWN:
                return this.worldObj.getCombinedLight(blockPos.down(), 0);
            default:
                return 0;
        }
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void onUpdate() {
        if (this.worldObj.isAirBlock(blockPos)) {
            this.setDead();
        } else if (this.paintList.isEmpty()) {
            this.setDead();
        }
        List<Paint> toRemove = Lists.newArrayList();
        for (Paint paint : paintList) {
            if (!worldObj.isAirBlock(blockPos.up()) && paint.facing == EnumFacing.UP) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockPos.down()) && paint.facing == EnumFacing.DOWN) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockPos.west()) && paint.facing == EnumFacing.WEST) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockPos.east()) && paint.facing == EnumFacing.EAST) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockPos.north()) && paint.facing == EnumFacing.NORTH) {
                toRemove.add(paint);
            } else if (!worldObj.isAirBlock(blockPos.south()) && paint.facing == EnumFacing.SOUTH) {
                toRemove.add(paint);
            }
        }
        this.paintList.removeAll(toRemove);
    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        int blockX = compound.getInteger("BlockX");
        int blockY = compound.getInteger("BlockY");
        int blockZ = compound.getInteger("BlockZ");
        this.blockPos = new BlockPos(blockX, blockY, blockZ);
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
        compound.setInteger("BlockX", this.blockPos.getX());
        compound.setInteger("BlockY", this.blockPos.getY());
        compound.setInteger("BlockZ", this.blockPos.getZ());
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
    public void writeSpawnData(ByteBuf buf) {
        buf.writeInt(this.blockPos.getX());
        buf.writeInt(this.blockPos.getY());
        buf.writeInt(this.blockPos.getZ());
        buf.writeInt(this.paintList.size());
        for (Paint paint : this.paintList) {
            paint.encode(buf);
        }
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        int blockX = buf.readInt();
        int blockY = buf.readInt();
        int blockZ = buf.readInt();
        this.blockPos = new BlockPos(blockX, blockY, blockZ);
        this.paintList = Lists.newArrayList();
        int paintListSize = buf.readInt();
        for (int i = 0; i < paintListSize; i++) {
            Paint paint = Paint.decode(buf);
            this.paintList.add(paint);
        }
    }
}
