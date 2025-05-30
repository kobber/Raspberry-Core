/*
 * Copyright (c) 2022 Team Galena

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cc.cassian.raspberry.compat.oreganized;

import cc.cassian.raspberry.compat.CavernsAndChasmsCompat;
import cc.cassian.raspberry.registry.RaspberryAttributes;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.teamabnormals.caverns_and_chasms.core.registry.CCParticleTypes;
import galena.oreganized.content.item.SilverMirrorItem;
import galena.oreganized.index.OSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.UUID;

public class OreganizedEvents {
    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        var stack = event.getItemStack();
        var mods = event.getModifiers();

        if (event.getSlotType() != EquipmentSlot.MAINHAND) return;

        if (stack.is(RaspberryTags.HAS_KINETIC_DAMAGE) && !mods.containsKey(RaspberryAttributes.KINETIC_DAMAGE.get())) {
            var damage = stack.getItem() instanceof DiggerItem item
                    ? item.getAttackDamage()
                    : stack.getItem() instanceof SwordItem item
                    ? item.getDamage()
                    : 2.0F;
            event.addModifier(RaspberryAttributes.KINETIC_DAMAGE.get(), new AttributeModifier(
                    UUID.fromString("0191ff58-54d7-711d-8a94-692379277c24"), "Kinetic Damage", damage / 3, AttributeModifier.Operation.ADDITION)
            );
        }
    }

    @SubscribeEvent
    public static void onHurtEvent(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(event.getSource().getDirectEntity() instanceof Player perp)) return;
        ItemStack held = perp.getMainHandItem();
        if (!(held.getItem() instanceof SilverMirrorItem)) return;

        Level level = victim.level;
        BlockPos origin = victim.blockPosition();
        BlockPos closestSilver = findNearestSilverBlock(level, origin, 24);
        if (closestSilver == null) return;

        for (int i = 0; i < 8; i++) {
            double offsetX = (victim.getRandom().nextDouble() - (0.5 * victim.getBbWidth())) * 1.5;
            double offsetY = victim.getRandom().nextDouble() * victim.getBbHeight();
            double offsetZ = (victim.getRandom().nextDouble() - (0.5 * victim.getBbWidth())) * 1.5;

            Vec3 spawnPos = victim.position().add(offsetX, offsetY, offsetZ);
            Vec3 targetPos = Vec3.atCenterOf(closestSilver);
            Vec3 velocity = targetPos.subtract(spawnPos).normalize().scale(victim.getRandom().nextDouble() * 0.15);
            if (ModList.get().isLoaded("caverns_and_chasms")) {
                level.addParticle(CavernsAndChasmsCompat.getSilverSpark(),
                        spawnPos.x, spawnPos.y, spawnPos.z,
                        velocity.x, velocity.y, velocity.z);
            }
        }

    }

    @Nullable
    private static BlockPos findNearestSilverBlock(Level level, BlockPos origin, int radius) {
        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
        BlockPos closest = null;
        double closestDistSq = Double.MAX_VALUE;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    searchPos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (!level.isLoaded(searchPos)) continue;
                    if (!level.getBlockState(searchPos).is(RaspberryTags.MIRROR_DETECTABLES)) continue;

                    double distSq = searchPos.distSqr(origin);
                    if (distSq < closestDistSq) {
                        closestDistSq = distSq;
                        closest = searchPos.immutable();
                    }
                }
            }
        }

        return closest;
    }

}
