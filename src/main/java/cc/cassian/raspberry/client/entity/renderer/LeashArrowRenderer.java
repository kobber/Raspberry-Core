package cc.cassian.raspberry.client.entity.renderer;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.entity.LeashArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LeashArrowRenderer extends ArrowRenderer<LeashArrowEntity> {
    private static final ResourceLocation LEASH_ARROW = RaspberryMod.locate("textures/entity/projectiles/leash_arrow.png");

    public LeashArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(LeashArrowEntity entity) {
        return LEASH_ARROW;
    }
}
