package com.expansemc.bending.plugin.command;

import com.expansemc.bending.api.ability.Ability;
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

public final class CommandBendingBind implements CommandExecutor {

    private static final Parameter.Value<Ability> ABILITY_PARAMETER =
            Parameter.registryElement(BendingTypeTokens.ABILITY_TOKEN, BendingRegistryTypes.ABILITY, "bending", "bending-classic")
                    .setKey("ability")
                    .build();

    public static final Command.Parameterized COMMAND = Command.builder()
            .parameter(ABILITY_PARAMETER)
            .setExecutor(new CommandBendingBind())
            .build();

    @Override
    public CommandResult execute(final CommandContext context) throws CommandException {
        final Ability ability = context.requireOne(ABILITY_PARAMETER);

        final ServerPlayer player = context.getCause().first(ServerPlayer.class)
                .orElseThrow(() -> new CommandException(Component.text("Must be a player to use this command.")));

        final int selectedSlotIndex = player.getInventory().getHotbar().getSelectedSlotIndex();
        player.offerSingle(BendingKeys.ABILITY_HOTBAR, selectedSlotIndex, ability);

        player.sendMessage(Component.text("Selected ability: ", NamedTextColor.GREEN)
                .append(ability.name()));

        return CommandResult.success();
    }
}