package mod.kerzox.automaton;

import com.google.common.collect.Queues;
import mod.kerzox.automaton.client.render.ter.PressurizedPipeRender;
import mod.kerzox.automaton.client.render.ter.TestGroupTileRender;
import mod.kerzox.automaton.common.commands.AutomatonCommand;
import mod.kerzox.automaton.common.events.ModBusEvents;
import mod.kerzox.automaton.common.network.PacketHandler;
import mod.kerzox.automaton.common.tile.machines.TestGroupTile;
import mod.kerzox.automaton.common.tile.transfer.pipes.PressurizedPipe;
import mod.kerzox.automaton.common.util.IBlockIsEntity;
import mod.kerzox.automaton.common.util.IRemovableTick;
import mod.kerzox.automaton.registry.AutomatonMaterials;
import mod.kerzox.automaton.registry.AutomatonRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static mod.kerzox.automaton.client.render.ter.PressurizedPipeRender.PIPE_RESOURCE;

@Mod("automaton")
public class Automaton
{
    private static final String MODID = "automaton";
    private static final Logger LOGGER = LogManager.getLogger();
    private static long tick = 0;
    public static Set<TileEntity> toTick = new HashSet<>();

    public Automaton() {
        PacketHandler.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientLoad);
        FMLJavaModLoadingContext.get().getModEventBus().register(new ModBusEvents());
        MinecraftForge.EVENT_BUS.register(this);
        AutomatonRegistry.init();
    }

    private void commonLoad(final FMLCommonSetupEvent event)
    {

    }

    private void clientLoad(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer((TileEntityType<TestGroupTile>) IBlockIsEntity.tileFromBlock(AutomatonMaterials.Tiles.testGroup), TestGroupTileRender::new);
        ClientRegistry.bindTileEntityRenderer((TileEntityType<PressurizedPipe>) IBlockIsEntity.tileFromBlock(AutomatonMaterials.Pipes.lowPressurePipe), PressurizedPipeRender::new);
        RenderTypeLookup.setRenderLayer(AutomatonMaterials.Pipes.lowPressurePipe, RenderType.cutout());
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static String modid() {
        return MODID;
    }



    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent e) {
        AutomatonCommand.register(e.getDispatcher());
    }

    @SubscribeEvent
    public void advanceTick(TickEvent.ServerTickEvent e) {
        if (!e.side.isServer()) {
            return;
        }
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        tick++;
    }

    @SubscribeEvent
    public void tickWorld(TickEvent.WorldTickEvent e) {
        if (!(e.world instanceof ServerWorld)) {
            return;
        }
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        if (!toTick.isEmpty()) {
            for (TileEntity te : new ArrayList<TileEntity>(toTick)) {
                IRemovableTick teR = (IRemovableTick) te;
                if (teR.tick()) {
                    toTick.remove(te);
                    teR.kill();
                }
                if (!teR.canTick()) {
                    toTick.remove(te);
                    teR.kill();
                }
            }
        }


//        if (!IRemovableTick.toTick.isEmpty()) {
//            Iterator<IRemovableTick> tickers = IRemovableTick.toTick.iterator();
//            while (tickers.hasNext()) {
//                IRemovableTick ticking =
//            }
//        }

//        for (IRemovableTick te : IRemovableTick.toTick) {
//            if (te != null) {
//                if (IRemovableTick.disabled.contains(te)) {
//                    te.kill();
//                } else {
//                    if (te.shouldTick()) {
//                        te.tick();
//                    }
//                }
//            }
//        }
    }
}
