package mod.kerzox.automaton;

import mod.kerzox.automaton.common.commands.AutomatonCommand;
import mod.kerzox.automaton.common.util.IRemovableTick;
import mod.kerzox.automaton.registry.AutomatonRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("automaton")
public class Automaton
{
    private static final String MODID = "automaton";
    private static final Logger LOGGER = LogManager.getLogger();

    public Automaton() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientLoad);
        MinecraftForge.EVENT_BUS.register(this);
        AutomatonRegistry.init();
    }

    private void commonLoad(final FMLCommonSetupEvent event)
    {

    }

    private void clientLoad(final FMLClientSetupEvent event) {

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
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != TickEvent.Phase.START) return;

        IRemovableTick.toTick.removeAll(IRemovableTick.toRemove);

        for (TileEntity te : IRemovableTick.toTick) {
            if (te instanceof IRemovableTick) { // redundant but oh well.
                IRemovableTick tickable = (IRemovableTick) te;
                if (tickable.tick()) IRemovableTick.remove(te);
            }
        }

        IRemovableTick.toTick.removeAll(IRemovableTick.toRemove);
    }
}
