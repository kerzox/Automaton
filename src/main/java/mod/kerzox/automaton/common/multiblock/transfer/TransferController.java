package mod.kerzox.automaton.common.multiblock.transfer;

import mod.kerzox.automaton.Automaton;
import mod.kerzox.automaton.common.util.IRemovableTick;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class TransferController<T extends TileEntity> {

    public static int ID;
    protected int id;

    protected ICache cache;
    protected World world;
    private T controllerBlock;
    private final List<T> network = new ArrayList<>();
    private final Set<TransferController<T>> merge = new HashSet<>();
    protected boolean doUpdate = false;
    protected boolean doRemoval = false;

    public TransferController(T te) {
        this.controllerBlock = te;
        this.world = te.getLevel();
        this.network.add(te);
        ID++;
        this.id = ID;
    }

    public T getControllerBlock() {
        return controllerBlock;
    }

    public void attach(T attaching) {
        IMultiblockAttachable<T> tile = (IMultiblockAttachable<T>) attaching;
        if (tile.getController() == this) return;
        tile.setController(this);
        this.network.add(attaching);
        afterAttach();
    }

    public void remove(T toRemove) {
        this.network.remove(toRemove);
        this.doRemoval = true;
    }

    public void afterAttach() {

    }

    public void controllerTick() {
        if (this.world.getBlockEntity(this.getControllerBlock().getBlockPos()) != this.getControllerBlock()) {
            this.network.remove(this.controllerBlock);
            if (this.network.size() == 0) {
                IRemovableTick.remove((IRemovableTick) this.getControllerBlock());
                return;
            }
            doRemovalProcess();
            IRemovableTick.remove((IRemovableTick) this.getControllerBlock());

            T newController = this.network.stream().findAny().get();
            ((IMultiblockAttachable<T>) newController).createController();

            this.network.remove(newController);

            for (T t : this.network) {
                ((IMultiblockAttachable<T>) t).setController(((IMultiblockAttachable<?>) newController).getController());
            }

            IRemovableTick.add(newController);

            return;
        }
        if (doRemoval) {
            if (this.network.size() == 0) {
                IRemovableTick.remove((IRemovableTick) this.getControllerBlock());
                return;
            }
            doRemovalProcess();
            doRemoval = false;
            Automaton.logger().info(this.network.size());
        }
        if (doUpdate) {
            onUpdate();
        }
    }

    private void doRemovalProcess() {

        // remove unconnected tiles from this controller

        for (T t : this.network) {
            Automaton.logger().info(t.getBlockPos());
        }

        List<T> unconnected = new ArrayList<>(this.network);
        List<T> connected = checkConnectionsFromTile(getReversedArray(unconnected));
        this.network.clear();
        unconnected.removeAll(connected);

        // null the controllers from the stragglers

        for (T t : unconnected) {
            ((IMultiblockAttachable<?>) t).setController(null);
            IRemovableTick.add(t);
        }

//        while(!unconnected.isEmpty()) {
//            T mainTile = unconnected.get(0);
//            List<T> otherConnections = checkConnectionsFromTile(getReversedArray(unconnected));
//            ((IMultiblockAttachable<T>) mainTile).createController();
//            TransferController<T> controller = ((IMultiblockAttachable<T>) mainTile).getController();
//            Iterator<T> iterator = unconnected.listIterator();
//            while(iterator.hasNext()) {
//                T tile = iterator.next();
//                if (otherConnections.contains(tile)) {
//                    iterator.remove();
//                }
//            }
//
//            for (T tile : otherConnections) {
//                if (tile != mainTile) {
//                    ((IMultiblockAttachable<?>) tile).setController(controller);
//                }
//            }
//            controller.network.remove(mainTile);
//            controller.network.addAll(removeDuplicates(otherConnections));
//            IRemovableTick.add(mainTile);
//        }
        this.network.addAll(removeDuplicates(connected));
    }

    private List<T> removeDuplicates(List<T> arr) {
        return arr.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private List<T> getReversedArray(List<T> arr) {
        Stack<T> temp = new Stack<>();
        for (T t : arr) {
            temp.push(t);
        }
        List<T> reversed = new ArrayList<>();
        while(!temp.isEmpty()) reversed.add(temp.pop());
        return reversed;
    }

    public List<T> checkConnectionsFromTile(List<T> tiles) {

        Stack<T> potentialConnections = new Stack<>();
        Stack<T> stack = new Stack<>();

        stack.addAll(tiles);
        List<T> connected = new ArrayList<>();
        T firstBlock = stack.pop();
        connected.add(firstBlock);

        while(!stack.empty()) {
            T fromStack = stack.pop();
            for (Direction dir : Direction.values()) {
                BlockPos pos = fromStack.getBlockPos();
                TileEntity foundTe = this.world.getBlockEntity(pos.relative(dir));
                if (foundTe != null) {
                    if (foundTe instanceof IMultiblockAttachable) {
                        if (foundTe == firstBlock || connected.contains((T) foundTe)) {
                            connected.add(fromStack);
                        } else {
                            potentialConnections.push(fromStack);
                        }
                    }
                }
            }
        }

        potentialConnections.removeAll(connected);
        List<T> reversed = getReversedArray(potentialConnections);
        potentialConnections.clear();
        potentialConnections.addAll(reversed);

        if (!potentialConnections.isEmpty()) {
            while (!potentialConnections.isEmpty()) {
                T fromStack = potentialConnections.pop();
                for (Direction dir : Direction.values()) {
                    BlockPos pos = fromStack.getBlockPos();
                    TileEntity foundTe = this.world.getBlockEntity(pos.relative(dir));
                    if (foundTe != null) {
                        if (foundTe instanceof IMultiblockAttachable) {
                            if (foundTe == firstBlock || connected.contains((T) foundTe)) {
                                connected.add(fromStack);
                            }
                        }
                    }
                }
            }
        }

        return connected;
    }

    public void onUpdate() {

    }

    public List<T> getNetwork() {
        return this.network;
    }

    public ICache getCache() {
        return cache;
    }

    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return LazyOptional.empty();
    }

    public int getId() {
        return id;
    }

    public void needsUpdate() {
        this.doUpdate = true;
    }

    public void needsRemoval() {
        this.doRemoval = true;
    }


}
