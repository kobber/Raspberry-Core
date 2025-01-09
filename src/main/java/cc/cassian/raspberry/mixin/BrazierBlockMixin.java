package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.teamabnormals.caverns_and_chasms.common.block.BrazierBlock;

import static vectorwing.farmersdelight.common.block.StoveBlock.LIT;

@Pseudo
@Mixin(BrazierBlock.class)
public abstract class BrazierBlockMixin {
    @Inject(
            method = "getStateForPlacement",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void startStovesLit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!ModConfig.get().braziersStartLit)
            cir.setReturnValue(cir.getReturnValue().setValue(LIT, false));
    }
}
