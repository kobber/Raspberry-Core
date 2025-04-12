package cc.cassian.raspberry.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowMixin {
    @Inject(method = "canEntityWalkOnPowderSnow", at = @At(value = "HEAD"), cancellable = true)
    private static void lightweightLeatherHorseArmor(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Horse horse) {
            if (horse.getArmor().is(Items.LEATHER_HORSE_ARMOR)) {
                cir.setReturnValue(true);
            }
        }
    }
}
