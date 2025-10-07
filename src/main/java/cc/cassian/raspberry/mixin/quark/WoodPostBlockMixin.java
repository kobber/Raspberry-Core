package cc.cassian.raspberry.mixin.quark;

import net.mehvahdjukaar.supplementaries.common.block.blocks.CandleHolderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.building.block.PaperLanternBlock;
import vazkii.quark.content.building.block.WoodPostBlock;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;

import static vazkii.quark.content.building.block.WoodPostBlock.CHAINED;
import static vectorwing.farmersdelight.common.block.CookingPotBlock.SUPPORT;

@Pseudo
@Mixin(WoodPostBlock.class)
public class WoodPostBlockMixin {
    @Inject(method = "getState", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void moreThingsCanHang(Level world, BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<BlockState> cir) {
        if(axis != Direction.Axis.Y) {
            BlockState downState = world.getBlockState(pos.relative(Direction.DOWN));
            BooleanProperty chainDownProp = CHAINED[Direction.DOWN.ordinal()];

            if(downState.getBlock() instanceof CookingPotBlock && downState.getValue(SUPPORT).equals(CookingPotSupport.HANDLE)) {
                cir.setReturnValue(cir.getReturnValue().setValue(chainDownProp, true));
            }
            if (downState.getBlock() instanceof CandleHolderBlock && downState.getValue(BlockStateProperties.ATTACH_FACE).equals(AttachFace.CEILING)) {
                cir.setReturnValue(cir.getReturnValue().setValue(chainDownProp, true));
            }
            if(downState.getBlock() instanceof PaperLanternBlock) {
                cir.setReturnValue(cir.getReturnValue().setValue(chainDownProp, true));
            }
        }

    }
}
