package cc.cassian.raspberry.entity;

import cc.cassian.raspberry.registry.RaspberryItems;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

import cc.cassian.raspberry.registry.RaspberryEntityTypes;

public class SwapArrowEntity extends AbstractArrow {
    public SwapArrowEntity(EntityType<? extends SwapArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SwapArrowEntity(Level level, double x, double y, double z) {
        super(RaspberryEntityTypes.SWAP_ARROW.get(), x, y, z, level);
    }

    public SwapArrowEntity(Level level, LivingEntity shooter) {
        super(RaspberryEntityTypes.SWAP_ARROW.get(), shooter, level);
    }

    public SwapArrowEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(RaspberryEntityTypes.SWAP_ARROW.get(), world);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity target = result.getEntity();
        Entity shooter = this.getOwner();

        if (target instanceof LivingEntity && shooter instanceof ServerPlayer) {
            Vec3 playerPos = shooter.position();
            Vec3 targetPos = target.position();

            shooter.teleportTo(targetPos.x, targetPos.y, targetPos.z);
            target.teleportTo(playerPos.x, playerPos.y, playerPos.z);

            this.level.playSound(null, shooter.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
            this.level.playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.inGround) {
            // Air particles
            Vec3 vec3 = this.getDeltaMovement();
            double dX = vec3.x;
            double dY = vec3.y;
            double dZ = vec3.z;
            float particleMovement = 0.02F;
            if (this.random.nextFloat() <= 0.5) {
                this.level.addParticle(ParticleTypes.REVERSE_PORTAL, this.getX() - dX, this.getY() - dY, this.getZ() - dZ, (this.random.nextDouble() - 0.5) * particleMovement, (this.random.nextDouble() - 0.5) * particleMovement, (this.random.nextDouble() - 0.5) * particleMovement);
            }
        } else {
            // Ground particles
            float particleMovement = 0.01F;
            if (this.random.nextFloat() <= 0.01) {
                this.level.addParticle(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 0.1, this.getZ(), (this.random.nextDouble() - 0.5) * particleMovement, this.random.nextDouble() * particleMovement, (this.random.nextDouble() - 0.5) * particleMovement);
            }
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(RaspberryItems.SWAP_ARROW.get());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return PlatformHelper.getEntitySpawnPacket(this);
    }
}
