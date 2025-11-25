package com.ortecfinance.tasklist.commands;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum Commands {

    SHOW("show"),
    ADD("add"),
    CHECK("check"),
    UNCHECK("uncheck"),
    HELP("help"),
    DEADLINE("deadline");

    private final String command;

    Commands(String command) {
        this.command = command;
    }

    public static Optional<Commands> fromString(String value) {
        for (Commands c : values()) {
            if (c.command.equalsIgnoreCase(value)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
}

