package cc.cassian.raspberry.mixin.supplementaries;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RakedGravelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.Block.pushEntitiesUp;

@Pseudo
@Mixin(RakedGravelBlock.class)
public class RakedGravelBlockMixin {
    @Inject(
            method = "turnToGravel",
            at = @At(value = "HEAD"),
    cancellable = true, remap = false)
    private static void turnToCorrectGravel(BlockState state, Level world, BlockPos pos, CallbackInfo ci) {
        if (state.is(RaspberryBlocks.getBlock(RaspberryBlocks.RAKED_BLACKSTONE_GRAVEL))) {
            world.setBlockAndUpdate(pos, pushEntitiesUp(state, RaspberryBlocks.getBlock(RaspberryBlocks.BLACKSTONE_GRAVEL).defaultBlockState(), world, pos));
            ci.cancel();
        }
        else if (state.is(RaspberryBlocks.getBlock(RaspberryBlocks.RAKED_DEEPSLATE_GRAVEL))) {
            world.setBlockAndUpdate(pos, pushEntitiesUp(state, RaspberryBlocks.getBlock(RaspberryBlocks.DEEPSLATE_GRAVEL).defaultBlockState(), world, pos));
            ci.cancel();
        }
    }
}
