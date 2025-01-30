package cc.cassian.raspberry.compat;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.onvoid.copperized.common.CopperizedArmorMaterials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CopperizedCompat {
    private static final Logger log = LogManager.getLogger(CopperizedCompat.class);

    public static void electrify(PlayerEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        int copperCount = 0;
        if (entity instanceof Player) {
            for (ItemStack armorSlot : entity.getArmorSlots()) {
                if (armorSlot.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial().equals(CopperizedArmorMaterials.COPPER)) {
                    copperCount++;
                }
            }
            copperCount--;
            if (entity.getLevel().isThundering() && copperCount >= 0) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, copperCount, false, false, true));
                if (copperCount > 0)
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, copperCount-1, false, false, true));
            }
        }


    }
}
