package cc.cassian.raspberry.mixin.oreganized;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryTags;
import galena.oreganized.content.ISilver;
import galena.oreganized.content.item.SilverMirrorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import javax.annotation.Nullable;

@Pseudo
@Mixin(SilverMirrorItem.class)
public class SilverMirrorItemMixin implements ISilver {
    @Override
    public int getUndeadDistance(Level world, BlockPos origin, @Nullable Player player, int frames) {
        // I'm gonna be honest I still don't really even know what frames means in this context, i assume it's like. the different possible blue textures ?
        final int radius = ModConfig.get().mirrorSearchRadius;
        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
        int radiusSq = radius * radius;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -ModConfig.get().mirrorVerticalSearchRadius; dy <= ModConfig.get().mirrorVerticalSearchRadius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSq) continue;

                    searchPos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);

                    if (world.getBlockState(searchPos).is(RaspberryTags.MIRROR_DETECTABLES)) {
                        double dist = player != null
                                ? Math.sqrt(searchPos.distToCenterSqr(player.position()))
                                : Math.sqrt(searchPos.distSqr(origin));

                        if (dist < 6.0) return 1;

                        int closeness = (int) Math.ceil(dist / (radius / (double) frames));
                        return Math.min(Math.max(closeness, 2), frames);
                    }
                }
            }
        }
        return frames;
    }
}
