package cc.cassian.raspberry.blocks.entity;

import cc.cassian.raspberry.blocks.TallCabinetBlock;
import cc.cassian.raspberry.registry.RaspberryBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.utility.TextUtils;

public class TallCabinetBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> contents;
    private ContainerOpenersCounter openersCounter;

    public TallCabinetBlockEntity(BlockPos pos, BlockState state) {
        super(RaspberryBlockEntityTypes.TALL_CABINET.get(), pos, state);
        this.contents = NonNullList.withSize(27, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                TallCabinetBlockEntity.this.playSound(state, ModSounds.BLOCK_CABINET_OPEN.get());
                TallCabinetBlockEntity.this.updateBlockState(state, true);
            }

            protected void onClose(Level level, BlockPos pos, BlockState state) {
                TallCabinetBlockEntity.this.playSound(state, ModSounds.BLOCK_CABINET_CLOSE.get());
                TallCabinetBlockEntity.this.updateBlockState(state, false);
            }

            protected void openerCountChanged(Level level, BlockPos pos, BlockState sta, int arg1, int arg2) {
            }

            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof ChestMenu) {
                    Container container = ((ChestMenu)player.containerMenu).getContainer();
                    return container == TallCabinetBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (!this.trySaveLootTable(compound)) {
            ContainerHelper.saveAllItems(compound, this.contents);
        }

    }

    public void load(CompoundTag compound) {
        super.load(compound);
        this.contents = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compound)) {
            ContainerHelper.loadAllItems(compound, this.contents);
        }

    }

    public int getContainerSize() {
        return 27;
    }

    protected NonNullList<ItemStack> getItems() {
        return this.contents;
    }

    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.contents = itemsIn;
    }

    protected Component getDefaultName() {
        return TextUtils.getTranslation("container.cabinet");
    }

    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return ChestMenu.threeRows(id, player, this);
    }

    public void startOpen(Player pPlayer) {
        if (this.level != null && !this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.incrementOpeners(pPlayer, this.level, this.getBlockPos(), this.getBlockState());
        }

    }

    public void stopOpen(Player pPlayer) {
        if (this.level != null && !this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.decrementOpeners(pPlayer, this.level, this.getBlockPos(), this.getBlockState());
        }

    }

    public void recheckOpen() {
        if (this.level != null && !this.remove) {
            this.openersCounter.recheckOpeners(this.level, this.getBlockPos(), this.getBlockState());
        }

    }

    void updateBlockState(BlockState state, boolean open) {
        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), state.setValue(TallCabinetBlock.OPEN, open), 3);
        }

    }

    // Do I need this from chestEntity?
    public static void swapContents(TallCabinetBlockEntity cabinet, TallCabinetBlockEntity otherCabinet) {
        NonNullList<ItemStack> nonnulllist = cabinet.getItems();
        cabinet.setItems(otherCabinet.getItems());
        otherCabinet.setItems(nonnulllist);
    }

    private void playSound(BlockState state, SoundEvent sound) {
        if (this.level != null) {
            Vec3i cabinetFacingVector = (state.getValue(TallCabinetBlock.FACING)).getNormal();
            double x = this.worldPosition.getX() + 0.5F + cabinetFacingVector.getX() / 2.0F;
            double y = this.worldPosition.getY() + 0.5F + cabinetFacingVector.getY() / 2.0F;
            double z = this.worldPosition.getZ() + 0.5F + cabinetFacingVector.getZ() / 2.0F;
            this.level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
        }
    }
}
