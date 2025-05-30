package cc.cassian.raspberry.mixin.oreganized;

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
        final int radius = 24;
        int closestCigar = frames;

        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

        outer: // first time doing outerloop stuff thanks stackoverflow
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    searchPos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);

                    if (world.getBlockState(searchPos).is(RaspberryTags.MIRROR_DETECTABLES)) {
                        double dist = player != null
                                ? Math.sqrt(searchPos.distToCenterSqr(player.position()))
                                : Math.sqrt(searchPos.distSqr(origin));

                        int closeness = (int) Math.ceil(dist / (radius / (double) frames));

                        if (dist < 6.0) {
                            closestCigar = 1;
                            break outer;
                        } else {
                            closestCigar = Math.min(Math.max(closeness, 2), closestCigar);
                        }
                    }
                }
            }
        }

        return closestCigar;
    }



}
