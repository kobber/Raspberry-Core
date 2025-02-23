package cc.cassian.raspberry.mixin.upgrade_aquatic;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.upgrade_aquatic.common.entity.monster.Thrasher;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;

@Pseudo
@Mixin(Thrasher.class)
public class ThrasherMixin {

    @Redirect(method = "removePassenger", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/upgrade_aquatic/common/entity/monster/Thrasher;getPassengers()Ljava/util/List;"))
    private List<Entity> removePassengerMixin(Thrasher instance) {
        return Collections.singletonList(instance.getFirstPassenger());
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/upgrade_aquatic/common/entity/monster/Thrasher;getPassengers()Ljava/util/List;"))
    private List<Entity> tickMixin(Thrasher instance) {
        return Collections.singletonList(instance.getFirstPassenger());
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean bypassTickNullPointer(Entity instance, TagKey<Fluid> fluidTag, Operation<Boolean> original) {
        if (instance == null) {
            return false;
        } else {
            return original.call(instance, fluidTag);
        }
    }

}
