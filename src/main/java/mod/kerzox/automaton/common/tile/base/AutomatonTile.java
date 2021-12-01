package mod.kerzox.automaton.common.tile.base;

import mod.kerzox.automaton.common.util.IBlockIsEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.ModelDataManager;

import javax.annotation.Nullable;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;

public class AutomatonTile<T extends AutomatonTile<T>> extends TileEntity {

    public AutomatonTile(Block block) {
        super(IBlockIsEntity.tileFromBlock(block));
    }

    public void update() {
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), BLOCK_UPDATE);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (pkt.getTag().contains("modelUpdate") && pkt.getTag().getBoolean("modelUpdate")) {
            ModelDataManager.requestModelDataRefresh(this);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), BLOCK_UPDATE + NOTIFY_NEIGHBORS);
        }
        handleUpdateTag(level.getBlockState(pkt.getPos()), pkt.getTag());
    }

    public void writeToClientPacket(CompoundNBT tag) {

    }

    public void readClientPacket(CompoundNBT tag) {

    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        writeToClientPacket(tag);
        return super.getUpdateTag();
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        readClientPacket(tag);
    }
}
