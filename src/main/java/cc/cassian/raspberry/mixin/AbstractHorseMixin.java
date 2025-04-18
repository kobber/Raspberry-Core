package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {
    @Inject(
            method = "isFood",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void bypassExpensiveCalculationIfNecessary(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(RaspberryTags.HORSE_FOOD)) {
            cir.setReturnValue(true);
        }
    }
}
