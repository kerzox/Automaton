package mod.kerzox.automaton.common.multiblock.transfer;

import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public interface IMultiblockAttachable<T extends TileEntity> {

    TransferController<T> getController();
    void setController(@Nullable TransferController<T> controller);

}
