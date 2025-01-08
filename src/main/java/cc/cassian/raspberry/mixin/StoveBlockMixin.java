package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.config.ModConfig;
import com.ninni.twigs.registry.TwigsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.utility.MathUtils;

import static vectorwing.farmersdelight.common.block.StoveBlock.LIT;

@Mixin(StoveBlock.class)
public abstract class StoveBlockMixin {

    @Inject(
            method = "getStateForPlacement",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void mixin(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!ModConfig.get().stovesStartLit)
            cir.setReturnValue(cir.getReturnValue().setValue(LIT, false));
    }

    @Inject(
            method = "use",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void mixin(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        var heldStack = player.getMainHandItem();
        var offhandStack = player.getOffhandItem();

        if (!state.getValue(BlockStateProperties.LIT)) {
            if (player.getMainHandItem().getItem().equals(TwigsItems.TWIG.get()) && offhandStack.getItem().equals(TwigsItems.TWIG.get())) {
                level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, MathUtils.RAND.nextFloat() * 0.4F + 0.8F);
                level.setBlock(pos, state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
                heldStack.shrink(1);
                offhandStack.shrink(1);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }
}
