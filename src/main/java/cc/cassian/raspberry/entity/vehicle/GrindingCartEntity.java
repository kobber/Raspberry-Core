package cc.cassian.raspberry.entity.vehicle;

import cc.cassian.raspberry.registry.RaspberryEntityTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class GrindingCartEntity extends ModifiedAbstractMinecart {
    private UUID pendingRider = null;

    public GrindingCartEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public GrindingCartEntity(Level level, double x, double y, double z) {
        super(RaspberryEntityTypes.GRINDING_CART.get(), level, x, y, z);
    }

    public void setPendingRider(Player player) {
        pendingRider = player.getUUID();
    }

    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            passenger.setPos(this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return passenger.position();
    }

    @Override
    protected Item getDropItem() {
        return null;
    }

    private void spawnGrindSparks() {
        Vec3 cartPos = this.position();
        Vec3 forward = this.getDeltaMovement().normalize();
        if (forward.lengthSqr() < 0.0001) {
            return;
        }

        Vec3 left = new Vec3(-forward.z, 0, forward.x).normalize();
        Vec3 right = left.scale(-1);

        double railOffset = 0.30 + random.nextDouble() * 0.1;
        double heightOffset = 0.01;

        Vec3 leftPos = cartPos.add(left.scale(railOffset)).add(0, heightOffset, 0);
        Vec3 rightPos = cartPos.add(right.scale(railOffset)).add(0, heightOffset, 0);

        Vec3 backwards = forward.scale(-0.2);

        Vec3 randVel = backwards.add(
                (random.nextDouble() - 0.5) * 0.5,
                0.1 + random.nextDouble() * 0.3,
                (random.nextDouble() - 0.5) * 0.5
        );

        double speed = this.getDeltaMovement().length();

        if(random.nextDouble() < speed) {
            level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    leftPos.x, leftPos.y, leftPos.z,
                    randVel.x, randVel.y, randVel.z);
        }
        if(random.nextDouble() < speed) {
            level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    rightPos.x, rightPos.y, rightPos.z,
                    randVel.x, randVel.y, randVel.z);
        }
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            spawnGrindSparks();
        }
        if (!this.level.isClientSide && pendingRider != null) {
            Entity rider = level.getPlayerByUUID(pendingRider);
            if (rider != null) {
                rider.startRiding(this, true);
            }
            pendingRider = null;
        }

        super.tick();

        if (!this.level.isClientSide) {
            /* TODO
                * Player jumping ejects
             */


            if (this.getPassengers().isEmpty() && pendingRider == null) {
                this.discard();
                return;
            }

            if (!isOnRail()) {
                ejectAndDiscard();
            }

            if (this.getDeltaMovement().length() < 0.1) {
                ejectAndDiscard();
            }

//            Vec3 deltaMovement = this.getDeltaMovement();
//            this.setDeltaMovement(deltaMovement.normalize().scale(0.4));

            // Maintain constant speed
    //        Vec3 current = this.getDeltaMovement();
    //        double hSpeed = Math.sqrt(current.x * current.x + current.z * current.z);
    //
    //        if (hSpeed > 0) {
    //            Vec3 dir = new Vec3(current.x / hSpeed, current.y, current.z / hSpeed);
    //            this.setDeltaMovement(dir.scale(this.targetSpeed));
    //        }

            // Player jump check (must also check client → server packet ideally)
    //        if (this.getControllingPassenger() instanceof Player player) {
    //            if (player.input.jumping) {  // client-side flag (use packet in real mod)
    //                detachAndRemove();
    //            }
    //        }
        }
    }

    private Boolean isOnRail() {
        int x = Mth.floor(this.getX());
        int y = Mth.floor(this.getY());
        int z = Mth.floor(this.getZ());
        if (this.level.getBlockState(new BlockPos(x, y - 1, z)).is(BlockTags.RAILS)) {
            --y;
        }

        BlockPos blockpos = new BlockPos(x, y, z);
        BlockState blockstate = this.level.getBlockState(blockpos);
        return this.canUseRail() && BaseRailBlock.isRail(blockstate);
    }

    private void ejectWithMomentum() {
        Vec3 vel = this.getDeltaMovement();
        Player player = (Player) this.getFirstPassenger();

        if (player != null) {
            this.ejectPassengers();
            if (player.isOnGround()) {
                player.setDeltaMovement(vel.x, 0.5, vel.z);
            } else {
                player.setDeltaMovement(vel);
            }
            player.hurtMarked = true;
        }
    }

    private void ejectAndDiscard() {
        this.ejectWithMomentum();
        this.discard();
    }
}