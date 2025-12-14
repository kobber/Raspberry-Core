package cc.cassian.raspberry.entity;

import cc.cassian.raspberry.compat.vanillabackport.leash.Leashable;
import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class LeashArrowEntity extends AbstractArrow {
    public LeashArrowEntity(EntityType<? extends LeashArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LeashArrowEntity(Level level, double x, double y, double z) {
        super(RaspberryEntityTypes.LEASH_ARROW.get(), x, y, z, level);
    }

    public LeashArrowEntity(Level level, LivingEntity shooter) {
        super(RaspberryEntityTypes.LEASH_ARROW.get(), shooter, level);
    }

    public LeashArrowEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(RaspberryEntityTypes.LEASH_ARROW.get(), world);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
//        super.onHitEntity(result);
        // TODO: Probably needs a few things from super.onHitEntity(result) still, like Enderman check

        if (this.getOwner() instanceof Player player) {
            Entity target = result.getEntity();

            if (target.isAlive() && target instanceof Leashable leashable) {
                if (!(leashable.getLeashHolder() instanceof Player)) {
                    if (!target.level.isClientSide && leashable.canHaveALeashAttachedTo(player)) {
                        if (leashable.isLeashed()) {
                            leashable.dropLeash(true, true);
                        }

                        leashable.setLeashedTo(player, true);
                        target.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
                        this.discard();
                    }
                }
            }
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(RaspberryItems.LEASH_ARROW.get());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return PlatformHelper.getEntitySpawnPacket(this);
    }
}
