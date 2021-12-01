package mod.kerzox.automaton.common.multiblock.transfer;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.tile.transfer.TransferTile;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mod.kerzox.automaton.Automaton.networkTick;

public class PipeNetwork {

    private long tick;
    private World world;
    private TransferTile controllerTile;
    private final Set<TransferTile> connected;
    protected ICache networkCache;

    private boolean destroy = false;

    public PipeNetwork(TransferTile tile) {
        this.controllerTile = tile;
        this.world = tile.getLevel();
        this.connected = new HashSet<>();
        buildNetwork();
    }

    public void update() {

    }

    public boolean tick() {
        // do stuff
        if (this.destroy || this.controllerTile.isRemoved() || world.getBlockEntity(this.controllerTile.getBlockPos()) == null) {
            return false;
        }
        update();
        tick++;
        return !destroy;
    }

    public static List<TileEntity> traverseFromTileToTile(TileEntity start, TileEntity destination) {
        List<TileEntity> traversed = new ArrayList<>();
        return recursiveSearchReturnAllTraversed(start, traversed, destination);
    }

    public static boolean reachableFromNode(TileEntity start, TileEntity destination) {
        Set<TileEntity> traversed = new HashSet<>();
        return recursiveSearch(start, traversed, destination) != null;
    }

    private static List<TileEntity> recursiveSearchReturnAllTraversed(TileEntity tile, List<TileEntity> traversed, TileEntity destination) {
        traversed.add(tile);
        if (tile == destination) return traversed;
        Automaton.logger().info(tile.getBlockPos());
        for (Direction dir : Direction.values()) {
            TileEntity te = destination.getLevel().getBlockEntity(tile.getBlockPos().relative(dir));
            if (te != null) {
                if (traversed.contains(te)) continue;
                return recursiveSearchReturnAllTraversed(te, traversed, destination);
            }
        }
        return traversed;
    }

    private static TileEntity recursiveSearch(TileEntity tile, Set<TileEntity> traversed, TileEntity destination) {
        traversed.add(tile);
        Automaton.logger().info(tile.getBlockPos());
        for (Direction dir : Direction.values()) {
            TileEntity te = destination.getLevel().getBlockEntity(tile.getBlockPos().relative(dir));
            if (te != null) {
                if (te == destination) return te;
                if (traversed.contains(te)) continue;
                return recursiveSearch(te, traversed, destination);
            }
        }
        return null;
    }

    private void setupMerge(TransferTile te) {
        Automaton.logger().info("Attempting merge of " + te.getNetwork() + " and " + this);
        PipeNetwork otherNetwork = te.getNetwork();
        otherNetwork.deconstruct(te);
        buildNetwork();
        read(otherNetwork.save());

    }

    public boolean addToNetwork(TransferTile tile) {
        onNetworkAdd(tile);
        if (tile.isRemoved() && !isValid(tile)) return false;
        if (needMerging(tile)) setupMerge(tile);
        tile.setNetwork(this);
        this.connected.add(tile);
        if (tile.getCachedControllerData() != null) {
            this.networkCache.read(tile.getCachedControllerData());
        }
        onNetworkAddFinish(tile);
        return true;
    }

    public void buildNetwork() {
        deconstructIgnoreController();
        if (this.destroy) this.destroy = false;
        networkTick.add(this);

        this.connected.add(controllerTile);

        for (Direction dir : Direction.values()) {
            TileEntity te = world.getBlockEntity(this.controllerTile.getBlockPos().relative(dir));
            if (te instanceof TransferTile) {
                if (!isValid(te)) continue;
                if (!addToNetwork((TransferTile) te)) continue;
                findConnectedFromTile(dir.getOpposite(), (TransferTile) te);
            }
        }
        for (TransferTile pipe : this.connected) {
            if (needMerging(pipe)) setupMerge(pipe);
        }
    }

    public boolean needMerging(TransferTile pipe) {
        return pipe.getNetwork() != null && pipe.getNetwork() != this;
    }

    public void findConnectedFromTile(Direction ignored, TransferTile te) {
        for (Direction dir : Direction.values()) {
            TileEntity nte = world.getBlockEntity(te.getBlockPos().relative(dir));
            if (nte instanceof TransferTile) {
                if (!isValid((TransferTile) nte)) continue;
                if (needMerging((TransferTile) nte)) setupMerge((TransferTile) nte);
                if (this.connected.contains((TransferTile) nte)) continue;
                if (!addToNetwork((TransferTile) nte)) continue;
                findConnectedFromTile(dir.getOpposite(), (TransferTile) nte);
            }
        }
    }

    public void deconstructIgnoreController() {
        destroy();
        for (TransferTile pipe : this.connected) {
            if (pipe == this.controllerTile && !pipe.isRemoved()) continue;
            pipe.setNetwork(null);
        }
        this.connected.clear();
    }

    public void deconstruct(TransferTile tile) {
        destroy();
        for (Direction dir : Direction.values()) {
            TileEntity te = world.getBlockEntity(tile.getBlockPos().relative(dir));
            if (isValid(te)) {
                if (reachableFromNode(this.controllerTile, te)) {
                    for (TileEntity tileEntity : traverseFromTileToTile(this.controllerTile, te)) {
                        tileEntity.serializeNBT();
                    }
                }
            }
        }
        for (TransferTile pipe : this.connected) {
            pipe.setNetwork(null);
        }
        this.connected.clear();
    }

    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return LazyOptional.empty();
    }

    public boolean isValid(TileEntity tile) {
        return true;
    }

    public void read(CompoundNBT nbt) {
        this.networkCache.read(nbt);
    }

    public CompoundNBT save() {
        CompoundNBT nbt = new CompoundNBT();
        return this.networkCache.save(nbt);
    }

    public ICache getCache() {
        return networkCache;
    }

    public void destroy() {
        this.destroy = true;
    }

    public Set<TransferTile> tiles() {
        return connected;
    }

    public TransferTile getController() {
        return this.controllerTile;
    }

    public void setControllerTile(TransferTile pipe) {
        this.controllerTile = pipe;
    }

    protected void onNetworkAdd(TileEntity tileBeingAdd) {

    }

    protected void onNetworkAddFinish(TileEntity tileAdded) {

    }

}


