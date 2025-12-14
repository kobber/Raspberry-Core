package cc.cassian.raspberry.items;

import cc.cassian.raspberry.entity.LeashArrowEntity;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class LeashArrowItem extends ArrowItem {
    public LeashArrowItem(Properties builder) {
        super(builder);

        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                LeashArrowEntity leash_arrow = new LeashArrowEntity(level, position.x(), position.y(), position.z());
                // TODO: Remove this before release
                leash_arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                return leash_arrow;
            }
        });
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
        return new LeashArrowEntity(worldIn, shooter);
    }
}
