package cc.cassian.raspberry.entity.vehicle;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IMinecartCollisionHandler;

import java.util.List;
import java.util.Map;

public abstract class ModifiedAbstractMinecart extends AbstractMinecart {
    private static final Map<RailShape, Pair<Vec3i, Vec3i>> EXITS;

    protected ModifiedAbstractMinecart(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    protected ModifiedAbstractMinecart(EntityType<?> entityType, Level level, double x, double y, double z) {
        super(entityType, level, x, y, z);
    }

    private static Pair<Vec3i, Vec3i> exits(RailShape shape) {
        return (Pair)EXITS.get(shape);
    }

    private boolean isRedstoneConductor(BlockPos pos) {
        return this.level.getBlockState(pos).isRedstoneConductor(this.level, pos);
    }

    @Override
    protected void moveAlongTrack(BlockPos pos, BlockState state) {
        this.resetFallDistance();
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        Vec3 vec3 = this.getPos(d0, d1, d2);
        d1 = (double)pos.getY();
        boolean onPoweredRail = false;
        boolean notOnPoweredRail = false;
        BaseRailBlock baserailblock = (BaseRailBlock)state.getBlock();
        if (baserailblock instanceof PoweredRailBlock && !((PoweredRailBlock)baserailblock).isActivatorRail()) {
            onPoweredRail = (Boolean)state.getValue(PoweredRailBlock.POWERED);
            notOnPoweredRail = !onPoweredRail;
        }

        double d3 = this.getSlopeAdjustment();
        if (this.isInWater()) {
            d3 *= 0.2;
        }

        // Slow down when ascending
        Vec3 vec31 = this.getDeltaMovement();
        RailShape railshape = ((BaseRailBlock)state.getBlock()).getRailDirection(state, this.level, pos, this);
        switch (railshape) {
            case ASCENDING_EAST:
                this.setDeltaMovement(vec31.add(-d3, (double)0.0F, (double)0.0F));
                ++d1;
                break;
            case ASCENDING_WEST:
                this.setDeltaMovement(vec31.add(d3, (double)0.0F, (double)0.0F));
                ++d1;
                break;
            case ASCENDING_NORTH:
                this.setDeltaMovement(vec31.add((double)0.0F, (double)0.0F, d3));
                ++d1;
                break;
            case ASCENDING_SOUTH:
                this.setDeltaMovement(vec31.add((double)0.0F, (double)0.0F, -d3));
                ++d1;
        }

        // Turn corners maybe? I'm not sure what this does
        vec31 = this.getDeltaMovement();
        Pair<Vec3i, Vec3i> pair = exits(railshape);
        Vec3i vec3i = (Vec3i)pair.getFirst();
        Vec3i vec3i1 = (Vec3i)pair.getSecond();
        double d4X = (double)(vec3i1.getX() - vec3i.getX());
        double d5Z = (double)(vec3i1.getZ() - vec3i.getZ());
        double d6 = Math.sqrt(d4X * d4X + d5Z * d5Z);
        double d7 = vec31.x * d4X + vec31.z * d5Z;
        if (d7 < (double)0.0F) {
            d4X = -d4X;
            d5Z = -d5Z;
        }

        double d8 = Math.min((double)2.0F, vec31.horizontalDistance());
        vec31 = new Vec3(d8 * d4X / d6, vec31.y, d8 * d5Z / d6);
        this.setDeltaMovement(vec31);
        Entity player = this.getFirstPassenger();
        // Player movement nudges cart
//        if (player instanceof Player) {
//            Vec3 playerMovement = player.getDeltaMovement();
//            double playerHorizontalDistanceSqr = playerMovement.horizontalDistanceSqr();
//            double cartHorizontalDistanceSqr = this.getDeltaMovement().horizontalDistanceSqr();
//            if (playerHorizontalDistanceSqr > 1.0E-4 && cartHorizontalDistanceSqr < 0.01) {
//                this.setDeltaMovement(this.getDeltaMovement().add(playerMovement.x * 0.1, (double)0.0F, playerMovement.z * 0.1));
//                notOnPoweredRail = false;
//            }
//        }

        // Set speed to zero if a player isn't nudging it
//        if (notOnPoweredRail && this.shouldDoRailFunctions()) {
//            double cartHorizontalDistance = this.getDeltaMovement().horizontalDistance();
//            if (cartHorizontalDistance < 0.03) {
//                this.setDeltaMovement(Vec3.ZERO);
//            } else {
//                // Does this just cut speed in half? Why?
//                this.setDeltaMovement(this.getDeltaMovement().multiply(0.5F, 0.0F, 0.5F));
//            }
//        }


        // Now what the hell is all of this?
        double d23X = (double)pos.getX() + (double)0.5F + (double)vec3i.getX() * (double)0.5F;
        double d10Z = (double)pos.getZ() + (double)0.5F + (double)vec3i.getZ() * (double)0.5F;
        double d12X = (double)pos.getX() + (double)0.5F + (double)vec3i1.getX() * (double)0.5F;
        double d13Z = (double)pos.getZ() + (double)0.5F + (double)vec3i1.getZ() * (double)0.5F;
        d4X = d12X - d23X;
        d5Z = d13Z - d10Z;
        double d14;
        if (d4X == (double)0.0F) {
            d14 = d2 - (double)pos.getZ();
        } else if (d5Z == (double)0.0F) {
            d14 = d0 - (double)pos.getX();
        } else {
            double d15 = d0 - d23X;
            double d16 = d2 - d10Z;
            d14 = (d15 * d4X + d16 * d5Z) * (double)2.0F;
        }

        d0 = d23X + d4X * d14;
        d2 = d10Z + d5Z * d14;
        this.setPos(d0, d1, d2);
        this.moveMinecartOnRail(pos);
        if (vec3i.getY() != 0 && Mth.floor(this.getX()) - pos.getX() == vec3i.getX() && Mth.floor(this.getZ()) - pos.getZ() == vec3i.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)vec3i.getY(), this.getZ());
        } else if (vec3i1.getY() != 0 && Mth.floor(this.getX()) - pos.getX() == vec3i1.getX() && Mth.floor(this.getZ()) - pos.getZ() == vec3i1.getZ()) {
            this.setPos(this.getX(), this.getY() + (double)vec3i1.getY(), this.getZ());
        }

        this.applyNaturalSlowdown();
        Vec3 vec33 = this.getPos(this.getX(), this.getY(), this.getZ());
        if (vec33 != null && vec3 != null) {
            double d17 = (vec3.y - vec33.y) * 0.05;
            Vec3 vec34 = this.getDeltaMovement();
            double d18 = vec34.horizontalDistance();
            if (d18 > (double)0.0F) {
                this.setDeltaMovement(vec34.multiply((d18 + d17) / d18, (double)1.0F, (d18 + d17) / d18));
            }

            this.setPos(this.getX(), vec33.y, this.getZ());
        }

        int j = Mth.floor(this.getX());
        int i = Mth.floor(this.getZ());
        if (j != pos.getX() || i != pos.getZ()) {
            Vec3 vec35 = this.getDeltaMovement();
            double d26 = vec35.horizontalDistance();
            this.setDeltaMovement(d26 * (double)(j - pos.getX()), vec35.y, d26 * (double)(i - pos.getZ()));
        }

        if (this.shouldDoRailFunctions()) {
            baserailblock.onMinecartPass(state, this.level, pos, this);
        }

        if (onPoweredRail && this.shouldDoRailFunctions()) {
            Vec3 vec36 = this.getDeltaMovement();
            double d27 = vec36.horizontalDistance();
            if (d27 > 0.01) {
                double d19 = 0.06;
                this.setDeltaMovement(vec36.add(vec36.x / d27 * 0.06, (double)0.0F, vec36.z / d27 * 0.06));
            } else {
                Vec3 vec37 = this.getDeltaMovement();
                double d20 = vec37.x;
                double d21 = vec37.z;
                if (railshape == RailShape.EAST_WEST) {
                    if (this.isRedstoneConductor(pos.west())) {
                        d20 = 0.02;
                    } else if (this.isRedstoneConductor(pos.east())) {
                        d20 = -0.02;
                    }
                } else {
                    if (railshape != RailShape.NORTH_SOUTH) {
                        return;
                    }

                    if (this.isRedstoneConductor(pos.north())) {
                        d21 = 0.02;
                    } else if (this.isRedstoneConductor(pos.south())) {
                        d21 = -0.02;
                    }
                }

                this.setDeltaMovement(d20, vec37.y, d21);
            }
        }

    }
    static {
        EXITS = (Map)Util.make(Maps.newEnumMap(RailShape.class), (enumMap) -> {
            Vec3i vec3i = Direction.WEST.getNormal();
            Vec3i vec3i1 = Direction.EAST.getNormal();
            Vec3i vec3i2 = Direction.NORTH.getNormal();
            Vec3i vec3i3 = Direction.SOUTH.getNormal();
            Vec3i vec3i4 = vec3i.below();
            Vec3i vec3i5 = vec3i1.below();
            Vec3i vec3i6 = vec3i2.below();
            Vec3i vec3i7 = vec3i3.below();
            enumMap.put(RailShape.NORTH_SOUTH, Pair.of(vec3i2, vec3i3));
            enumMap.put(RailShape.EAST_WEST, Pair.of(vec3i, vec3i1));
            enumMap.put(RailShape.ASCENDING_EAST, Pair.of(vec3i4, vec3i1));
            enumMap.put(RailShape.ASCENDING_WEST, Pair.of(vec3i, vec3i5));
            enumMap.put(RailShape.ASCENDING_NORTH, Pair.of(vec3i2, vec3i7));
            enumMap.put(RailShape.ASCENDING_SOUTH, Pair.of(vec3i6, vec3i3));
            enumMap.put(RailShape.SOUTH_EAST, Pair.of(vec3i3, vec3i1));
            enumMap.put(RailShape.SOUTH_WEST, Pair.of(vec3i3, vec3i));
            enumMap.put(RailShape.NORTH_WEST, Pair.of(vec3i2, vec3i));
            enumMap.put(RailShape.NORTH_EAST, Pair.of(vec3i2, vec3i1));
        });
    };
}
