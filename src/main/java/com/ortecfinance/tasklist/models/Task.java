package com.ortecfinance.tasklist.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public final class Task {

    private final long id;
    private final String description;
    private boolean done;
    private LocalDate deadline;
    private String projectName;

    public Task(long id, String description, boolean done, LocalDate deadline, String projectName) {
        this.id = id;
        this.description = description;
        this.done = done;
        this.deadline = deadline;
        this.projectName = projectName;
    }
}
