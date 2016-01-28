package net.ilexiconn.paintbrush.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class PaintedBlockRenderer extends RenderEntity {
    public int frame;

    @Override
    public void doRender(Entity entity, double posX, double posY, double posZ, float yaw, float partialTicks) {
        if (entity instanceof PaintedBlockEntity) {
            PaintedBlockEntity paintedBlockEntity = (PaintedBlockEntity) entity;
            if (frame % 120 == 1) {
                System.out.println(entity.getEntityId() + ": " + paintedBlockEntity.paintList.size());
            }
        }
        frame++;
    }
}
