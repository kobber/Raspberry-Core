package cc.cassian.raspberry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class IronStoveBlock extends StoveBlock {

    public IronStoveBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(2, 0, 2, 14, 16, 14);
    }

    @Override
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource rand) {
        if (stateIn.getValue(CampfireBlock.LIT)) {
            double x = pos.getX() + 0.5F;
            double y = pos.getY();
            double z = pos.getZ() + 0.5F;
            if (rand.nextInt(10) == 0) {
                level.playLocalSound(x, y, z, ModSounds.BLOCK_STOVE_CRACKLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

//            if (rand.nextDouble() >= 0.75) {
//                Direction direction = stateIn.getValue(HorizontalDirectionalBlock.FACING);
//                Direction.Axis direction$axis = direction.getAxis();
//                double horizontalOffset = rand.nextDouble() * 0.6 - 0.3;
//                double xOffset = direction$axis == Direction.Axis.X ? direction.getStepX() * 0.4 : horizontalOffset;
//                double yOffset = (8.0F + rand.nextDouble() * 6.0F) / 16.0F;
//                double zOffset = direction$axis == Direction.Axis.Z ? direction.getStepZ() * 0.4 : horizontalOffset;
//                level.addParticle(ParticleTypes.SMOKE, x + xOffset, y + yOffset, z + zOffset, 0.0F, 0.0F, 0.0F);
//                level.addParticle(ParticleTypes.FLAME, x + xOffset, y + yOffset, z + zOffset, 0.0F, 0.0F, 0.0F);
//            }
        }
    }
}
