package com.expansemc.bending.plugin.command;

import org.spongepowered.api.command.Command;

public final class CommandBending {

    public static final Command.Parameterized COMMAND = Command.builder()
            .addChild(CommandBendingBind.COMMAND, "bind", "b")
            .addChild(CommandBendingPassive.COMMAND, "passive", "p")
            .build();
}