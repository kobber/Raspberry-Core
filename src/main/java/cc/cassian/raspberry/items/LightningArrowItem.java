package cc.cassian.raspberry.items;

import cc.cassian.raspberry.entity.LightningArrowEntity;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class LightningArrowItem extends ArrowItem {
    public LightningArrowItem(Properties builder) {
        super(builder);

        DispenserBlock.registerBehavior(this, new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack stack) {
                LightningArrowEntity lightning_arrow = new LightningArrowEntity(level, position.x(), position.y(), position.z());
                lightning_arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                return lightning_arrow;
            }
        });
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
        return new LightningArrowEntity(worldIn, shooter);
    }
}
