package mod.kerzox.automaton.common.network;

import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class PipeConnectionData {

    private BlockPos pos;
    private byte[] data;

    public PipeConnectionData(PacketBuffer buf) {
        pos = buf.readBlockPos();
        data = buf.readByteArray();
    }

    public PipeConnectionData(BlockPos pos, List<Byte> dataIn) {
        this.pos = pos;
        data = new byte[dataIn.size()];
        for (int i = 0; i < dataIn.size(); i++) {
            data[i] = dataIn.get(i);
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeByteArray(data);
    }

    public static void handle(PipeConnectionData packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(packet, ctx));
            else {
                /*
                    Check if the blockpos is actually still a position while on the server
                 */
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    World world = player.getLevel();
                    if (world.getBlockEntity(packet.pos) != null) {
                        if (world.getBlockEntity(packet.pos) instanceof PressurizedPipe) {
                            PacketHandler.sendToClient(packet, player);
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void handleOnClient(PipeConnectionData packet, Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().level != null) {
            TileEntity te = Minecraft.getInstance().level.getBlockEntity(packet.pos);
            if (te instanceof PressurizedPipe) {
                ((PressurizedPipe) te).getConnections().clear();
                for (byte b : packet.data) {
                    ((PressurizedPipe) te).getConnections().add(Direction.values()[b]);
                }
            }
        }
    }

}
