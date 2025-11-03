package cc.cassian.raspberry.client;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.registry.BlockSupplier;
import cc.cassian.raspberry.client.registry.RaspberryItemProperties;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RaspberryMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RaspberryModClient {
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event){
        BlockColors blockColors = event.getBlockColors();
        for (BlockSupplier block : RaspberryBlocks.FOLIAGE_BLOCKS) {
            event.register(((state, view, pos, tintIndex) -> {
                if (view == null || pos == null) {
                    return 9551193;
                }
                return BiomeColors.getAverageFoliageColor(view, pos);
            }), block.getBlock());
        }
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RaspberryEntityTypes.ASHBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RaspberryEntityTypes.ROSE_GOLD_BOMB.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(RaspberryBlocks.TEMPORARY_COBWEB.get(), RenderType.cutout());
        });

        RaspberryItemProperties.register();
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.RegisterAdditional event) {
        // This should probably be extracted into a handler of some kind
        event.register(new ResourceLocation(RaspberryMod.MOD_ID,"block/cheery_wildflowers_potted"));
        event.register(new ResourceLocation(RaspberryMod.MOD_ID,"block/moody_wildflowers_potted"));
        event.register(new ResourceLocation(RaspberryMod.MOD_ID,"block/playful_wildflowers_potted"));
        event.register(new ResourceLocation(RaspberryMod.MOD_ID,"block/hopeful_wildflowers_potted"));
    }
}
