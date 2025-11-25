package com.ortecfinance.tasklist.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class Project {

    private final String name;
    private final List<Task> tasks;

    public Project(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
    }
}
