package com.ortecfinance.tasklist.console;

import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Collection;

public class ConsoleInputOutput {

    private final BufferedReader in;
    private final PrintWriter out;

    public ConsoleInputOutput(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void help() {
        out.println("Commands:");
        out.println("  show");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println("  deadline <task ID> <date DD-MM-YYYY>");
        out.println();
    }

    public void error(String command) {
        out.printf("I don't know what the command \"%s\" is.", command);
        out.println();
    }

    public void printProjectNotFoundByName(String projectName) {
        out.printf("Could not find a project with the name \"%s\".", projectName);
        out.println();
    }

    public void printTaskNotFoundById(long taskId) {
        out.printf("Could not find a task with an ID of %d.%n", taskId);
        out.println();
    }

    public void showProjectsAndTasks(Collection<Project> projects) {
        for (Project project : projects) {
            out.println(project.getName());
            for (Task task : project.getTasks()) {
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }
    }
}
