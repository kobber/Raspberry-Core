package cc.cassian.raspberry.mixin.supplementaries;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.NeapolitanCompat;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamabnormals.neapolitan.core.Neapolitan;
import com.teamabnormals.neapolitan.core.registry.NeapolitanMobEffects;
import net.mehvahdjukaar.supplementaries.common.block.ModBlockProperties;
import net.mehvahdjukaar.supplementaries.common.block.blocks.PancakeBlock;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PancakeBlock.class)
public class PancakeBlockMixin {
    @Shadow
    @Final
    public static EnumProperty<ModBlockProperties.Topping> TOPPING;

    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private int toppingsGiveMoreFood(int foodLevelModifier, @Local BlockState state) {
        if (state.getValue(TOPPING) != ModBlockProperties.Topping.NONE)
            return 4;
        return 3;
    }

    @ModifyArg(method = "removeLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/world/effect/MobEffect;I)V"))
    private MobEffect pancakesGiveSugarRush(MobEffect par1) {
        if (ModCompat.NEAPOLITAN) {
            return NeapolitanCompat.sugarRush();
        } else {
            return par1;
        }
    }
}
