package net.ilexiconn.paintbrush.client.render;

import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TextureData {
    private DynamicTexture[] paintTexture;
    private int[][] paintTextureData;
    private ResourceLocation[] paintTextureLocation;

    public TextureData(PaintedBlockEntity entity) {
        this.paintTexture = new DynamicTexture[EnumFacing.VALUES.length];
        this.paintTextureData = new int[EnumFacing.VALUES.length][];
        this.paintTextureLocation = new ResourceLocation[EnumFacing.VALUES.length];
        for (int i = 0; i < EnumFacing.values().length; i++) {
            this.paintTexture[i] = new DynamicTexture(16, 16);
            this.paintTextureData[i] = this.paintTexture[i].getTextureData();
            this.paintTextureLocation[i] = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("paint/" + entity.blockPos.getX() + entity.blockPos.getY() + entity.blockPos.getZ() + i, this.paintTexture[i]);
        }
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                setColor(EnumFacing.NORTH, x, y, 0xFFFFFFFF);
            }
        }
    }

    public DynamicTexture getPaintTexture(EnumFacing facing) {
        return this.paintTexture[facing.ordinal()];
    }

    public int[] getPaintTextureData(EnumFacing facing) {
        return this.paintTextureData[facing.ordinal()];
    }

    public void setColor(EnumFacing facing, int x, int y, int color) {
        this.paintTextureData[facing.ordinal()][(16 * y) + x] = color;
        this.getPaintTexture(facing).updateDynamicTexture();
    }

    public ResourceLocation getPaintTextureLocation(EnumFacing facing) {
        return this.paintTextureLocation[facing.ordinal()];
    }
}
