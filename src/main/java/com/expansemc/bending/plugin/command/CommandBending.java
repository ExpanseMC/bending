package com.expansemc.bending.plugin.command;

import org.spongepowered.api.command.Command;

public final class CommandBending {

    public static final Command.Parameterized COMMAND = Command.builder()
            .child(CommandBendingBind.COMMAND, "bind", "b")
            .build();
}