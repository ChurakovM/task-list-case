package com.ortecfinance.tasklist.commands;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum Commands {

    SHOW("show", "show"),
    ADD("add", "add project <name> \nadd task <project> <description>"),
    CHECK("check", "check <task ID>"),
    UNCHECK("uncheck", "uncheck <task ID>"),
    HELP("help", "help"),
    DEADLINE("deadline", "deadline <task ID> <date DD-MM-YYYY>"),
    TODAY("today", "today"),
    VIEW_BY_DEADLINE("view-by-deadline", "view-by-deadline");

    private final String command;
    private final String helpText;

    Commands(String command, String helpText) {
        this.command = command;
        this.helpText = helpText;
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

