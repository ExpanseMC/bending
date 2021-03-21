package com.expansemc.bending.plugin.command;

import com.expansemc.bending.api.ability.Ability;
import com.expansemc.bending.api.ability.AbilityControls;
import com.expansemc.bending.api.data.BendingKeys;
import com.expansemc.bending.api.registry.BendingRegistryTypes;
import com.expansemc.bending.api.util.BendingTypeTokens;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Set;

public final class CommandBendingPassive implements CommandExecutor {

    private static final Parameter.Value<Ability> ABILITY_PARAMETER =
            Parameter.registryElement(BendingTypeTokens.ABILITY_TOKEN, BendingRegistryTypes.ABILITY, "bending", "bending-classic")
                    .key("ability")
                    .build();

    public static final Command.Parameterized COMMAND = Command.builder()
            .addParameter(ABILITY_PARAMETER)
            .executor(new CommandBendingPassive())
            .build();

    @Override
    public CommandResult execute(final CommandContext context) throws CommandException {
        final Ability ability = context.requireOne(ABILITY_PARAMETER);

        if (!ability.controls().contains(AbilityControls.PASSIVE.get())) {
            throw new CommandException(Component.text("That is not a passive ability."));
        }

        final ServerPlayer player = context.cause().first(ServerPlayer.class)
                .orElseThrow(() -> new CommandException(Component.text("Must be a player to use this command.")));

        if (player.getOrElse(BendingKeys.PASSIVE_ABILITIES, Set.of()).contains(ability)) {
            context.cause().audience().sendMessage(Component.text("Added passive: ", NamedTextColor.GREEN).append(ability.name()));
            player.removeSingle(BendingKeys.PASSIVE_ABILITIES, ability);
        } else {
            context.cause().audience().sendMessage(Component.text("Removed passive: ", NamedTextColor.GREEN).append(ability.name()));
            player.offerSingle(BendingKeys.PASSIVE_ABILITIES, ability);
        }

        return CommandResult.success();
    }
}