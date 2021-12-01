package mod.kerzox.automaton.common.tile.transfer.conveyor;

import mod.kerzox.automaton.common.block.ConveyorBeltBlock;
import mod.kerzox.automaton.common.capabilities.item.ItemInputWrapper;
import mod.kerzox.automaton.common.capabilities.item.ItemOutputWrapper;
import mod.kerzox.automaton.common.capabilities.item.SidedInventoryHandler;
import mod.kerzox.automaton.common.tile.base.AutomatonTile;
import mod.kerzox.automaton.common.util.INeighbourUpdatable;
import mod.kerzox.automaton.common.util.IRemovableTick;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static mod.kerzox.automaton.common.block.ConveyorBeltBlock.*;

public class ConveyorBelt extends AutomatonTile<ConveyorBelt> implements IRemovableTick, INeighbourUpdatable {

    public static final ModelProperty<BlockState> CONVEYOR_STATE = new ModelProperty<>();
    private BlockState tileState;

    private boolean ticking = true;
    private Map<Direction, ConveyorBeltBlock.ConnectiveSides> cachedSides = new HashMap<>();
    private SidedInventoryHandler inventory = new SidedInventoryHandler(new ItemInputWrapper(), new ItemOutputWrapper());
    private final LazyOptional<SidedInventoryHandler> inventoryHandler = LazyOptional.of(() -> inventory);

    public ConveyorBelt(Block block) {
        super(block);
    }

    @Override
    public boolean tick() {
        if (level != null) {
            if (!level.isClientSide) {
                return pushItems(level, getBlockPos());
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(CONVEYOR_STATE, tileState).build();
    }

    @Override
    public void writeToClientPacket(CompoundNBT tag) {
        if (tileState != null) {
            tag.put("state", NBTUtil.writeBlockState(tileState));
        }
    }

    @Override
    public void readClientPacket(CompoundNBT tag) {
        if (tag.contains("state")) {
            tileState = NBTUtil.readBlockState(tag.getCompound("state"));
        }
    }

    @Override
    public void updateByNeighbours(BlockState state, BlockState neighbourState, BlockPos pos, BlockPos neighbour) {
        Map<Direction, ConnectiveSides> sides = Arrays.stream(Direction.values()).collect(Collectors.toMap(face -> face, face -> ConnectiveSides.NONE, (a, b) -> b));
        getAllConnectedSides(pos, sides);

        Direction blockFacing = state.getValue(FACING);

        AtomicBoolean hideRightLeg = new AtomicBoolean(false);
        AtomicBoolean hideLeftLeg = new AtomicBoolean(false);

        sides.forEach((dir, side) -> {
            if (side == ConnectiveSides.CONNECTED) {
                TileEntity te = level.getBlockEntity(pos.relative(dir));
                if (te instanceof ConveyorBelt) {
                        Direction connectedTeFacing = te.getBlockState().getValue(FACING);
                        if (connectedTeFacing != blockFacing) {
                            if (connectedTeFacing.getAxisDirection() == Direction.AxisDirection.POSITIVE)
                                hideRightLeg.set(true);
                            else hideLeftLeg.set(true);
                        }
                }
            }
        });


    }

    private void getAllConnectedSides(BlockPos pos, Map<Direction, ConnectiveSides> sides) {
        for (Direction dir : Direction.values()) {
            TileEntity te = level.getBlockEntity(pos.relative(dir));
            if (te instanceof ConveyorBelt) {
                sides.put(dir, ConnectiveSides.CONNECTED);
            }
        }
    }


//    private void doConnections(BlockState state, World world, BlockPos pos) {
//        boolean update = false;
//        Map<Direction, ConveyorBeltBlock.ConnectiveSides> sides = new HashMap<>();
//        for (Direction face : Direction.values()) {
//            sides.put(face, ConveyorBeltBlock.ConnectiveSides.NONE);
//        }
//        Direction facing = state.getValue(FACING);
//        for (Direction dir : Direction.values()) {
//            TileEntity te = world.getBlockEntity(pos.relative(dir));
//            //if (facing.getAxis() != dir.getAxis()) continue;
//            if (te instanceof ConveyorBelt) {
//                sides.put(dir, ConveyorBeltBlock.ConnectiveSides.CONNECTED);
//            }
//        }
//        boolean leg = true;
//        if (sides.get(Direction.EAST) == ConveyorBeltBlock.ConnectiveSides.CONNECTED && sides.get(Direction.WEST) == ConveyorBeltBlock.ConnectiveSides.CONNECTED ||
//                sides.get(Direction.NORTH) == ConveyorBeltBlock.ConnectiveSides.CONNECTED && sides.get(Direction.SOUTH) == ConveyorBeltBlock.ConnectiveSides.CONNECTED) {
//            leg = false;
//        }
//        for (Direction face : Direction.values()) {
//            if (cachedSides.get(face) != sides.get(face))
//                update = true;
//        }
//        cachedSides = sides;
//        tileState = state.setValue(NORTH, sides.get(Direction.NORTH))
//                .setValue(EAST, sides.get(Direction.EAST))
//                .setValue(SOUTH, sides.get(Direction.SOUTH))
//                .setValue(WEST, sides.get(Direction.WEST))
//                .setValue(LEG_RENDER, leg);
//        if (update) {
//            world.setBlocksDirty(pos, getBlockState(), tileState);
//            world.setBlock(pos, tileState, BLOCK_UPDATE + NOTIFY_NEIGHBORS);
//            this.setChanged();
//        }
//    }

    private boolean pushItems(World world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            TileEntity te = world.getBlockEntity(pos.relative(dir));
            if (te != null) {
                Optional<IItemHandler> handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
                if (handler.isPresent()) {
                    for (int i = 0; i < handler.get().getSlots(); i++) {
                        ItemStack simulated = inventory.extractItem(1, 1, true);
                        if (simulated.isEmpty()) return false;
                        ItemStack stack = inventory.extractItem(1, 1, false);
                        if (handler.get().insertItem(i, stack, true).isEmpty()) {
                            handler.get().insertItem(i, stack, false);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            Boolean[] sided = inventory.checkSidesOfHandlers(side);
            if (side != null) {
                if (sided[0] && sided[1]) return inventoryHandler.cast();
                else if (sided[0]) return LazyOptional.of(() -> inventory.getHandlerFromIndex(0)).cast();
                else if (sided[1]) return LazyOptional.of(() -> inventory.getHandlerFromIndex(1)).cast();
            }
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void kill() {
        setTicking(false);
    }

    @Override
    public boolean canTick() {
        return this.ticking;
    }

    @Override
    public void setTicking(boolean bool) {
        this.ticking = bool;
    }
}
