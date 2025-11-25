package com.ortecfinance.tasklist.commands;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum SubCommand {

    PROJECT("project"),
    TASK("task");

    private final String subCommand;

    SubCommand(String subCommand) {
        this.subCommand = subCommand;
    }

    public static Optional<SubCommand> fromString(String value) {
        for (SubCommand sc : values()) {
            if (sc.subCommand.equalsIgnoreCase(value)) {
                return Optional.of(sc);
            }
        }
        return Optional.empty();
    }
}
