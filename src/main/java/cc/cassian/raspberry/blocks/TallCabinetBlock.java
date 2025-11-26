package cc.cassian.raspberry.blocks;

import javax.annotation.Nullable;

import cc.cassian.raspberry.blocks.entity.TallCabinetBlockEntity;
import cc.cassian.raspberry.blocks.state.CabinetType;
import cc.cassian.raspberry.registry.RaspberryBlockEntityTypes;
import cc.cassian.raspberry.registry.RaspberryBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;
import java.util.function.BiPredicate;

public class TallCabinetBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final EnumProperty<CabinetType> TYPE;
    public static final BooleanProperty OPEN;

    // Double stuff
    private static final DoubleBlockCombiner.Combiner<TallCabinetBlockEntity, Optional<Container>> CHEST_COMBINER;
    private static final DoubleBlockCombiner.Combiner<TallCabinetBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER;

    public TallCabinetBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, CabinetType.SINGLE).setValue(OPEN, false));
    }

    @Nullable
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return this.combine(state, level, pos, false).apply(MENU_PROVIDER_COMBINER).orElse(null);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
            if (menuprovider != null) {
                player.openMenu(menuprovider);
            }

            return InteractionResult.CONSUME;
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof Container) {
                Containers.dropContents(level, pos, (Container)tileEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }

    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof TallCabinetBlockEntity) {
            ((TallCabinetBlockEntity)tileEntity).recheckOpen();
        }

    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (neighborState.is(this) && direction.getAxis().isVertical()) {
            CabinetType neighborType = neighborState.getValue(TYPE);
            if (state.getValue(TYPE) == CabinetType.SINGLE && neighborType != CabinetType.SINGLE && state.getValue(FACING) == neighborState.getValue(FACING) && getConnectedDirection(neighborState) == direction.getOpposite()) {
                return state.setValue(TYPE, neighborType.getOpposite());
            }
        } else if (getConnectedDirection(state) == direction) {
            return state.setValue(TYPE, CabinetType.SINGLE);
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        CabinetType cabinetType = CabinetType.SINGLE;
        Direction direction = context.getHorizontalDirection().getOpposite();
        boolean shiftClick = context.isSecondaryUseActive();
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isVertical() && shiftClick) {
            Direction candidatePartnerFacing = this.candidatePartnerFacing(context, clickedFace.getOpposite());
            if (candidatePartnerFacing != null) {
                direction = candidatePartnerFacing;
                cabinetType = clickedFace == Direction.DOWN ? CabinetType.BOTTOM : CabinetType.TOP;
            }
        }

        if (cabinetType == CabinetType.SINGLE && !shiftClick) {
            if (direction == this.candidatePartnerFacing(context, Direction.DOWN)) {
                cabinetType = CabinetType.TOP;
            } else if (direction == this.candidatePartnerFacing(context, Direction.UP)) {
                cabinetType = CabinetType.BOTTOM;
            }
        }

        return this.defaultBlockState().setValue(FACING, direction).setValue(TYPE, cabinetType);
    }

    @Nullable
    private Direction candidatePartnerFacing(BlockPlaceContext context, Direction direction) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
        return blockstate.is(this) && blockstate.getValue(TYPE) == CabinetType.SINGLE ? blockstate.getValue(FACING) : null;
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof TallCabinetBlockEntity) {
                ((TallCabinetBlockEntity)tileEntity).setCustomName(stack.getHoverName());
            }
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, TYPE, OPEN);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(this, blockState, level, pos, false));
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RaspberryBlockEntityTypes.TALL_CABINET.get().create(pos, state);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    public static Container getContainer(TallCabinetBlock cabinet, BlockState state, Level level, BlockPos pos, boolean override) {
        return (Container)((Optional)cabinet.combine(state, level, pos, override).apply(CHEST_COMBINER)).orElse((Container)null);
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState state) {
        CabinetType cabinetType = state.getValue(TYPE);
        if (cabinetType == CabinetType.SINGLE) {
            return DoubleBlockCombiner.BlockType.SINGLE;
        } else {
            return cabinetType == CabinetType.TOP ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
        }
    }

    public static Direction getConnectedDirection(BlockState state) {
        return state.getValue(TYPE) == CabinetType.TOP ? Direction.DOWN : Direction.UP;
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends TallCabinetBlockEntity> combine(BlockState state, Level level, BlockPos pos, boolean override) {
        BiPredicate<LevelAccessor, BlockPos> bipredicate;
        bipredicate = (arg, arg2) -> false;

        return DoubleBlockCombiner.combineWithNeigbour(RaspberryBlockEntityTypes.TALL_CABINET.get(), TallCabinetBlock::getBlockType, TallCabinetBlock::getConnectedDirection, FACING, state, level, pos, bipredicate);
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        TYPE = RaspberryBlockStateProperties.CABINET_TYPE;
        OPEN = BlockStateProperties.OPEN;
        CHEST_COMBINER = new DoubleBlockCombiner.Combiner<TallCabinetBlockEntity, Optional<Container>>() {
            public Optional<Container> acceptDouble(TallCabinetBlockEntity arg, TallCabinetBlockEntity arg2) {
                return Optional.of(new CompoundContainer(arg, arg2));
            }

            public Optional<Container> acceptSingle(TallCabinetBlockEntity arg) {
                return Optional.of(arg);
            }

            public Optional<Container> acceptNone() {
                return Optional.empty();
            }
        };
        MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<TallCabinetBlockEntity, Optional<MenuProvider>>() {
            public Optional<MenuProvider> acceptDouble(final TallCabinetBlockEntity arg, final TallCabinetBlockEntity arg2) {
                final Container container = new CompoundContainer(arg, arg2);
                return Optional.of(new MenuProvider() {
                    @Nullable
                    public AbstractContainerMenu createMenu(int i, Inventory argx, Player arg2x) {
                        if (arg.canOpen(arg2x) && arg2.canOpen(arg2x)) {
                            arg.unpackLootTable(argx.player);
                            arg2.unpackLootTable(argx.player);
                            return ChestMenu.sixRows(i, argx, container);
                        } else {
                            return null;
                        }
                    }

                    public Component getDisplayName() {
                        if (arg.hasCustomName()) {
                            return arg.getDisplayName();
                        } else {
                            return arg2.hasCustomName() ? arg2.getDisplayName() : Component.translatable("container.raspberry.tallCabinet");
                        }
                    }
                });
            }

            public Optional<MenuProvider> acceptSingle(TallCabinetBlockEntity arg) {
                return Optional.of(arg);
            }

            public Optional<MenuProvider> acceptNone() {
                return Optional.empty();
            }
        };
    }
}
