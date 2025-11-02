package cc.cassian.raspberry.blocks;

import java.util.stream.IntStream;

import cc.cassian.raspberry.blocks.properties.HorizontalConnectionType;
import cc.cassian.raspberry.blocks.properties.ModBlockStateProperties;
import com.starfish_studios.another_furniture.util.block.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SturdyStairBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<HorizontalConnectionType> TYPE = ModBlockStateProperties.HORIZONTAL_CONNECTION_TYPE;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape TOP_STEP = Block.box(0, 13, 8, 16, 16, 16);
    protected static final VoxelShape BOTTOM_STEP = Block.box(0, 5, 0, 16, 8, 8);
    protected static final VoxelShape NORTH_SHAPE = Shapes.or(TOP_STEP, BOTTOM_STEP);
    protected static final VoxelShape EAST_SHAPE = ShapeUtil.rotateShape(NORTH_SHAPE, Direction.EAST);
    protected static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotateShape(NORTH_SHAPE, Direction.SOUTH);
    protected static final VoxelShape WEST_SHAPE = ShapeUtil.rotateShape(NORTH_SHAPE, Direction.WEST);

    public SturdyStairBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, HorizontalConnectionType.SINGLE)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);

        if (direction == Direction.NORTH) {
            return NORTH_SHAPE;
        } else if (direction == Direction.EAST) {
            return EAST_SHAPE;
        } else if (direction == Direction.SOUTH) {
            return SOUTH_SHAPE;
        } else {
            return WEST_SHAPE;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        BlockState blockstate = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        return blockstate.setValue(TYPE, getShapeType(blockstate, context.getLevel(), blockpos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        return state.setValue(TYPE, getShapeType(state, level, currentPos));
    }

    private static HorizontalConnectionType getShapeType(BlockState state, BlockGetter level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Direction left = facing.getClockWise();
        Direction right = facing.getCounterClockWise();

        BlockState left_neighbor = level.getBlockState(pos.relative(left));
        BlockState right_neighbor = level.getBlockState(pos.relative(right));
        boolean connect_left = left_neighbor.getBlock() instanceof SturdyStairBlock && left_neighbor.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing;
        boolean connect_right = right_neighbor.getBlock() instanceof SturdyStairBlock && right_neighbor.getValue(BlockStateProperties.HORIZONTAL_FACING) == facing;
        HorizontalConnectionType type = connect_left && connect_right ?
                HorizontalConnectionType.MIDDLE
                : (connect_right ? HorizontalConnectionType.LEFT
                : (connect_left ? HorizontalConnectionType.RIGHT
                : HorizontalConnectionType.SINGLE));
        return type;
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }
}
