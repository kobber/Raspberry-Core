package cc.cassian.raspberry.mixin.minecraft;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(
            method = "onHitBlock", at = @At(value = "HEAD"), cancellable = true)
    public void ricochet(BlockHitResult result, CallbackInfo ci) {
        var arrow = (AbstractArrow) (Object) this;
        // TODO: Test for enchantment

        Vec3 movement = arrow.getDeltaMovement();
        double minSpeed = 1;
        double minSpeedSqr = minSpeed * minSpeed;

        if (movement.lengthSqr() > minSpeedSqr) {
            Direction face = result.getDirection();

            // convert face normal to Vec3
            Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());

            double dot = movement.dot(normal);
            Vec3 reflected = movement.subtract(normal.scale(2 * dot)).scale(0.7);

            arrow.setDeltaMovement(reflected);

            // Rotate arrow
            Vec3 newMovement = arrow.getDeltaMovement();
            arrow.setYRot((float)(Math.atan2(newMovement.x, newMovement.z) * (180F / Math.PI)));
            arrow.setXRot((float)(Math.atan2(newMovement.y, newMovement.horizontalDistance()) * (180F / Math.PI)));

            arrow.yRotO = arrow.getYRot();
            arrow.xRotO = arrow.getXRot();

            BlockState blockState = arrow.level.getBlockState(result.getBlockPos());
            arrow.playSound(blockState.getSoundType().getHitSound(), 1.0F, 1.2F / (arrow.level.random.nextFloat() * 0.2F + 0.9F));
            ci.cancel();
        }
    }

}
