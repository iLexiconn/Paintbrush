package net.ilexiconn.paintbrush.client;

import net.ilexiconn.paintbrush.Paintbrush;
import net.ilexiconn.paintbrush.client.render.PaintedBlockRenderer;
import net.ilexiconn.paintbrush.server.ProxyServer;
import net.ilexiconn.paintbrush.server.entity.PaintedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyServer {
    private Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onInit() {
        super.onInit();

        EventHandlerClient eventHandler = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(eventHandler);
        RenderingRegistry.registerEntityRenderingHandler(PaintedBlockEntity.class, new PaintedBlockRenderer(Minecraft.getMinecraft().getRenderManager()));
    }

    @Override
    public void onPostInit() {
        mc.getRenderItem().getItemModelMesher().register(Paintbrush.paintbrush, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation("paintbrush:paintbrush", "inventory");
            }
        });
        mc.getRenderItem().getItemModelMesher().register(Paintbrush.paintScraper, 0, new ModelResourceLocation("paintbrush:paint_scraper", "inventory"));
    }
}
