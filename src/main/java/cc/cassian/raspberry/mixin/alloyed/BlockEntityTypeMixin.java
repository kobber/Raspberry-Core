package cc.cassian.raspberry.mixin.alloyed;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At(value = "RETURN"), cancellable = true)
    private void forceAllowAlloyed(BlockState arg, CallbackInfoReturnable<Boolean> cir) {
        if (Objects.requireNonNullElse(ForgeRegistries.BLOCKS.getKey(arg.getBlock()), ResourceLocation.fromNamespaceAndPath("minecraft", "air")).getNamespace().equals("alloyed"))
            cir.setReturnValue(true);
    }
}
