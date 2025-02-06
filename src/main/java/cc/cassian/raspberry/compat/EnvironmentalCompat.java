package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.entity.ai.goal.HuntWormGoal;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.environmental.core.other.EnvironmentalProperties;
import com.teamabnormals.environmental.core.registry.EnvironmentalParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

public class EnvironmentalCompat {

    public static BlockBehaviour.Properties getTruffleProperties() {
        return EnvironmentalProperties.BURIED_TRUFFLE;
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        Level level = event.getLevel();
        RandomSource random = level.getRandom();

        if (target.getType().is(RaspberryTags.WORM_SEEKERS) && target.isAlive() && target instanceof Animal seeker) {
            IDataManager data = ((IDataManager) target);
            if (!seeker.isLeashed() && stack.is(RaspberryTags.WORM_SEEKER_ITEMS) && !seeker.isBaby()) {
                if (data.getValue(RaspberryMod.WORM_HUNTING_TIME) == 0) {
                    if (level.dimensionType().natural()) {
                        data.setValue(RaspberryMod.WORM_HUNTING_TIME, 4800);
                        if (!event.getEntity().isCreative()) stack.shrink(1);

                        if (level.isClientSide()) {
                            for (int i = 0; i < 7; ++i) {
                                double d0 = random.nextGaussian() * 0.02D;
                                double d1 = random.nextGaussian() * 0.02D;
                                double d2 = random.nextGaussian() * 0.02D;
                                // TODO: replace particles because these look piggy ofc
                                level.addParticle(EnvironmentalParticleTypes.PIG_FINDS_TRUFFLE.get(), target.getRandomX(1.0D), target.getRandomY() + 0.5D, target.getRandomZ(1.0D), d0, d1, d2);
                            }
                        }
                    } else if (level.isClientSide()) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = random.nextGaussian() * 0.02D;
                            double d1 = random.nextGaussian() * 0.02D;
                            double d2 = random.nextGaussian() * 0.02D;
                            level.addParticle(ParticleTypes.SMOKE, target.getRandomX(1.0D), target.getRandomY() + 0.5D, target.getRandomZ(1.0D), d0, d1, d2);
                        }
                    }

                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().is(RaspberryTags.WORM_SEEKERS) && entity instanceof Animal seeker) {
            Set<WrappedGoal> goals = seeker.goalSelector.getAvailableGoals();
            if (goals.stream().noneMatch((goal) -> goal.getGoal() instanceof HuntWormGoal))
                seeker.goalSelector.addGoal(2, new HuntWormGoal(seeker));
        }
    }

}
