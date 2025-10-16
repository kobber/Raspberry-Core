package cc.cassian.raspberry.mixin.minecraft;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartChest.class)
public class MinecartChestMixin {
    @Inject(method = "getDefaultDisplayBlockState", at = @At("RETURN"), cancellable = true)
    public void useBarrelBlockstate(CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, Direction.UP));
    }
}
