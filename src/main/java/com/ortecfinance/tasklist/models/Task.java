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
    private Project project;

    public Task(long id, String description, boolean done, LocalDate deadline, Project project) {
        this.id = id;
        this.description = description;
        this.done = done;
        this.deadline = deadline;
        this.project = project;
    }
}
