package cc.cassian.raspberry.mixin.create;

import com.simibubi.create.content.contraptions.MountedStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CabinetBlockEntity;

@Pseudo
@Mixin(MountedStorage.class)
public class MountedStorageMixin {
    @Inject(method = "canUseAsStorage", at = @At(value = "HEAD"), cancellable = true)
    private static void cabinetsAreStorage(BlockEntity be, CallbackInfoReturnable<Boolean> cir) {
        if (be instanceof CabinetBlockEntity)
            cir.setReturnValue(true);
    }
}
