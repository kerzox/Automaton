package mod.kerzox.automaton.common.tile.transfer.pipes;

import mod.kerzox.automaton.common.multiblock.transfer.PipeNetwork;
import mod.kerzox.automaton.common.multiblock.transfer.fluid.FluidPipeNetwork;
import mod.kerzox.automaton.common.tile.transfer.TransferTile;
import mod.kerzox.automaton.common.util.INeighbourUpdatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PressurizedPipe extends TransferTile implements INeighbourUpdatable {

    private Map<Direction, TileEntity> connectedTiles = new HashMap<>();
    public PressurizedPipe(Block block) {
        super(block);
    }

    public FluidPipeNetwork createNetwork() {
        return new FluidPipeNetwork(this);
    }

    @Override
    public void setNetwork(PipeNetwork network) {
        this.network = network;
    }

    @Override
    public void updateByNeighbours(BlockState state, BlockState neighbourState, BlockPos pos, BlockPos neighbour) {
        this.connectedTiles.clear();
        this.connectedPipes.clear();
        for (Direction dir : Direction.values()) {
            TileEntity te = level.getBlockEntity(pos.relative(dir));
            if (te instanceof PressurizedPipe) {
                this.connectedPipes.put(dir, (PressurizedPipe) te);
            }
            if (te != null && !(te instanceof PressurizedPipe)) {
                this.connectedTiles.put(dir, te);
            }
        }
        attemptToConnectOrBuildNetwork();
    }

    private void attemptToConnectOrBuildNetwork() {
        if (!level.isClientSide) {
            for (Direction dir : Direction.values()) {
                TileEntity te = level.getBlockEntity(getBlockPos().relative(dir));
                if (te instanceof PressurizedPipe) {
                    if (((PressurizedPipe) te).getNetwork() != null) {
                        ((PressurizedPipe) te).getNetwork().addToNetwork(this);
                        return;
                    }
                }
            }
            if (this.network != null) {
                this.getNetwork().findGasTiles(getConnectedTiles());
                return;
            }
            this.network = createNetwork();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!level.isClientSide) {
            if (this.network != null) {
                this.network.deconstruct(this);
            }
        }
    }

    @Override
    public void deserializeNBT(BlockState state, CompoundNBT nbt) {
        super.deserializeNBT(state, nbt);
        if (nbt.contains("network")) {

        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        if (this.network != null) {
            nbt.put("network", this.network.save());
            if (this.cachedControllerData == null) cachedControllerData = new CompoundNBT();
            cachedControllerData.put("cache", this.network.save());
        }
        return nbt;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return this.network != null ? this.network.getCapability(cap, side) : super.getCapability(cap, side);
    }

    public Map<Direction, TileEntity> getConnectedTiles() {
        return connectedTiles;
    }

    @Override
    public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
        super.load(p_230337_1_, p_230337_2_);
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        return super.save(pCompound);
    }

    @Override
    public FluidPipeNetwork getNetwork() {
        return (FluidPipeNetwork) network;
    }

    @Override
    public Map<Direction, TransferTile> getConnectedPipes() {
        return connectedPipes;
    }
}
