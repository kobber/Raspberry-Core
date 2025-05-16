package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.compat.GlidersCompat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public class ArmorStandMixin {
    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    public void doNotTakeGliders(Player player, Vec3 vec, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (GlidersCompat.isGlider(stack)) {
            cir.setReturnValue(InteractionResult.FAIL);
            cir.cancel();
        }
    }
}
