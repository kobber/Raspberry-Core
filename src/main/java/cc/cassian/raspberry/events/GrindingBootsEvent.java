package cc.cassian.raspberry.events;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.entity.vehicle.GrindingCartEntity;
import cc.cassian.raspberry.items.FlowerGarlandItem;
import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GrindingBootsEvent {
    private static final Map<UUID, Boolean> wasOnGround = new HashMap<>();


    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        Level level = player.level;

        if (player.isPassenger() && player.getVehicle() instanceof GrindingCartEntity) {
            player.setPose(Pose.CROUCHING);
        }

        if (event.side.isClient()) return;
        if (player.isPassenger()) return;
//        if (!(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof FlowerGarlandItem)) return;

        boolean prev = wasOnGround.getOrDefault(player.getUUID(), false);
        boolean isOnGround = player.isOnGround();

        if (!prev && isOnGround) {
            BlockPos blockPos = player.blockPosition();
            if (BaseRailBlock.isRail(level, blockPos)) {
                spawnGrindingCart(player, blockPos);
            }
        }

        wasOnGround.put(player.getUUID(), isOnGround);
    }

    private static void spawnGrindingCart(Player player, BlockPos railPos) {
        Level level = player.level;

        Vec3 playerMovement = player.getDeltaMovement();
        Direction playerDir = player.getMotionDirection();
        double speed = Math.sqrt(playerMovement.x * playerMovement.x + playerMovement.z * playerMovement.z) * 6;

        // Not going fast enough
        if (speed < 0.1) {
            return;
        }

        Vec3 spawnLocation;

        // If player is moving on z axis, center spawned cart on X axis, and vice versa
        if (playerDir.getAxis() == Direction.Axis.Z) {
            spawnLocation = new Vec3(railPos.getX() + 0.5, player.position().y + 0.0625, player.position().z + playerMovement.z);
        } else {
            spawnLocation = new Vec3(player.position().x+ playerMovement.x, player.position().y + 0.0625, railPos.getZ() + 0.5);
        }

        // Actually don't center (just testing)
        spawnLocation = new Vec3(player.position().x+ playerMovement.x, player.position().y + 0.0625, player.position().z + playerMovement.z);

        GrindingCartEntity cart = new GrindingCartEntity(level,
                spawnLocation.x(), spawnLocation.y(), spawnLocation.z());
        cart.setYRot(playerDir.toYRot());
        cart.yRotO = playerDir.toYRot();

//        Vec3 playerVel = player.getDeltaMovement();
//        Vec3 vel;
//        Vec3 direction;
//
//        if (Math.abs(playerVel.x) > Math.abs(playerVel.z)) {
//            direction = new Vec3(playerVel.x > 0 ? 1 : -1, 0, 0);
//        } else {
//            direction = new Vec3(0, 0, playerVel.z > 0 ? 1 : -1);
//        }


        cart.setDeltaMovement(new Vec3(playerDir.getStepX(), 0, playerDir.getStepZ()).scale(speed));
//        cart.setDeltaMovement(new Vec3(playerMovement.x, 0, playerMovement.z).scale(4));
        level.addFreshEntity(cart);
        cart.setPendingRider(player);
//        player.startRiding(cart, true);
    }
}
