package mod.kerzox.automaton.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import mod.kerzox.automaton.common.capabilities.gas.GasUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Optional;

public class DebugCommands {

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> d) {
        LiteralArgumentBuilder<CommandSource> cmd;
        return Commands.literal("debug")
                .then(Commands.literal("gas")
                    .then(Commands.literal("heat").then(Commands.argument("amount", MessageArgument.message()).executes(DebugCommands::heatGas)))
                .then(Commands.literal("temperature").executes(DebugCommands::getTemperature)));
    }

    private static int getTemperature(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty()) {
            Optional<FluidStack> fluidOpt = FluidUtil.getFluidContained(stack);
            if (fluidOpt.isPresent()) {
                FluidStack fluidStack = fluidOpt.get();
                context.getSource().sendSuccess(new TranslationTextComponent(fluidStack.getTranslationKey()).append(new StringTextComponent("Current Temperature: "+GasUtil.getTemperatureOfGas(fluidStack))), false);
            }
        }
        return 1;
    }

    private static int heatGas(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty()) {
            Optional<FluidStack> fluidOpt = FluidUtil.getFluidContained(stack);
            if (fluidOpt.isPresent()) {
                FluidStack fluidStack = fluidOpt.get();
                try {
                    GasUtil.heatGas(fluidStack, Float.parseFloat(MessageArgument.getMessage(context, "amount").getString()));
                } catch (NumberFormatException exception) {
                    context.getSource().sendFailure(new StringTextComponent("Amount must be a number"));
                }
            }
        }
        return 1;
    }
}
