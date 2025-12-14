package cc.cassian.raspberry.entity;


import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import cc.cassian.raspberry.registry.RaspberryItems;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

import java.util.List;

public class LightningArrowEntity extends AbstractArrow {
    private boolean extraParticles = false;
    public LightningArrowEntity(EntityType<? extends LightningArrowEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LightningArrowEntity(Level level, double x, double y, double z) {
        super(RaspberryEntityTypes.LIGHTNING_ARROW.get(), x, y, z, level);
    }

    public LightningArrowEntity(Level level, LivingEntity shooter) {
        super(RaspberryEntityTypes.LIGHTNING_ARROW.get(), shooter, level);
    }

    public LightningArrowEntity(PlayMessages.SpawnEntity spawnEntity, Level world) {
        this(RaspberryEntityTypes.LIGHTNING_ARROW.get(), world);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        summonLightning(result.getEntity().blockPosition());

        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.setXRot(270);
        this.xRotO = this.getXRot();
        super.onHitBlock(result);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide()) return;
        if (this.inGround){
            if(this.random.nextFloat() < 0.01) {
                summonLightning(this.blockPosition());
            }
        }
    }

    private void summonLightning(BlockPos blockPos) {
        if (this.level.isClientSide()) return;

        Level level = this.level;
        boolean isRaining = level.isRainingAt(blockPos);
        boolean hasSky = level.canSeeSky(blockPos);
        if (hasSky && isRaining) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
            bolt.moveTo(Vec3.atBottomCenterOf(blockPos));
            level.addFreshEntity(bolt);
            this.discard();
        }
    }

    private static Vec3 rotateAroundAxis(Vec3 vec, Vec3 axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return vec.scale(cos)
                .add(axis.cross(vec).scale(sin))
                .add(axis.scale(axis.dot(vec) * (1 - cos)));
    }

//    @Override
//    public void tick() {
//        if (this.isCritArrow()) {
//            this.setCritArrow(false);
//            this.extraParticles = true;
//        }
//
//        super.tick();
//
//        // No particles on first tick
//        if (this.firstTick) return;
//
//        if (this.inGround) {
//            // Ground particles
//            float particleMovement = 0.01F;
//            if (this.random.nextFloat() <= 0.01) {
//                this.level.addParticle(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 0.1, this.getZ(), (this.random.nextDouble() - 0.5) * particleMovement, this.random.nextDouble() * particleMovement, (this.random.nextDouble() - 0.5) * particleMovement);
//            }
//        } else {
//            // Air particles
//            Vec3 vec3 = this.getDeltaMovement();
//            double dX = vec3.x;
//            double dY = vec3.y;
//            double dZ = vec3.z;
//            if (this.extraParticles) {
//                // Spiraling particles
//                int particlesPerTick = 8;
//                double radius = 0.1;
//                double rotationPerTick = Math.toRadians(30);
//
//                Vec3 direction = vec3.normalize();
//                Vec3 perpendicular = direction.cross(new Vec3(0, 1, 0));
//                if (perpendicular.lengthSqr() < 1.0E-6) {
//                    perpendicular = direction.cross(new Vec3(1, 0, 0));
//                }
//                perpendicular = perpendicular.normalize();
//
//                for(int i = 0; i < particlesPerTick; ++i) {
//                    if (this.random.nextFloat() <= 0.75) {
//                        double angle = (this.tickCount * particlesPerTick - i) * rotationPerTick;
//                        Vec3 rotated = rotateAroundAxis(perpendicular, direction, angle);
//                        Vec3 pos = this.position();
//                        pos = pos.subtract(dX * i / particlesPerTick,dY * i / particlesPerTick,dZ * i / particlesPerTick );
//                        pos = pos.add(rotated.scale(radius));
//                        this.level.addParticle(
//                                RaspberryParticleTypes.SWAP_ARROW_PORTAL.get(),
//                                pos.x,
//                                pos.y,
//                                pos.z,
//                                rotated.scale(0.03 * (1 - this.random.nextFloat() * 0.4)).x,
//                                rotated.scale(0.03 * (1 - this.random.nextFloat() * 0.4)).y,
//                                rotated.scale(0.03 * (1 - this.random.nextFloat() * 0.4)).z
//                        );
//                    }
//                }
//            } else if (this.random.nextFloat() <= 0.5) {
//                float particleMovement = 0.02F;
//                this.level.addParticle(ParticleTypes.REVERSE_PORTAL, this.getX() - dX, this.getY() - dY, this.getZ() - dZ, (this.random.nextDouble() - 0.5) * particleMovement, (this.random.nextDouble() - 0.5) * particleMovement, (this.random.nextDouble() - 0.5) * particleMovement);
//            }
//        }
//    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(RaspberryItems.LIGHTNING_ARROW.get());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return PlatformHelper.getEntitySpawnPacket(this);
    }
}
