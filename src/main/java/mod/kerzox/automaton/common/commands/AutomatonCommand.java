package mod.kerzox.automaton.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mod.kerzox.automaton.Automaton;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class AutomatonCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmd = dispatcher.register(
                Commands.literal(Automaton.modid())

        );
        dispatcher.register(Commands.literal("automaton").redirect(cmd));
    }

}
