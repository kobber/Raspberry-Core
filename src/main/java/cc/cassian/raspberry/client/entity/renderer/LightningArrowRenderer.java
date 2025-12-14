package cc.cassian.raspberry.client.entity.renderer;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.entity.LightningArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightningArrowRenderer extends ArrowRenderer<LightningArrowEntity> {
    private static final ResourceLocation LIGHTNING_ARROW = RaspberryMod.locate("textures/entity/projectiles/lightning_arrow.png");

    public LightningArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(LightningArrowEntity entity) {
        return LIGHTNING_ARROW;
    }
}
