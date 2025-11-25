package com.ortecfinance.tasklist;

import com.ortecfinance.tasklist.commands.Commands;
import com.ortecfinance.tasklist.commands.SubCommand;
import com.ortecfinance.tasklist.console.ConsoleInputOutput;
import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;
import com.ortecfinance.tasklist.storage.ProjectsStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public final class TaskList implements Runnable {

    private static final String QUIT = "quit";
    private final ProjectsStorage projectsStorage = new ProjectsStorage();
    private static ConsoleInputOutput consoleInputOutput;

    private final BufferedReader in;
    private final PrintWriter out;

    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        new TaskList(in, out).run();
    }

    public TaskList(BufferedReader reader, PrintWriter writer) {
        this.in = reader;
        this.out = writer;
        consoleInputOutput = new ConsoleInputOutput(in, out);
    }

    public void run() {
        out.println("Welcome to TaskList! Type 'help' for available commands.");
        while (true) {
            out.print("> ");
            out.flush();
            String command;
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (command.equals(QUIT)) {
                break;
            }
            execute(command);
        }
    }

    private void execute(String commandLine) {
        String[] commandRest = commandLine.split(" ", 2);
        String commandString = commandRest[0];

        Commands
                .fromString(commandString)
                .ifPresentOrElse(
                        cmd -> handleCommand(cmd, commandRest),
                        () -> consoleInputOutput.error(commandString)
                );
    }

    private void handleCommand(Commands cmd, String[] commandRest) {
        switch (cmd) {
            case SHOW -> show();
            case ADD -> add(commandRest[1]);
            case CHECK -> check(commandRest[1]);
            case UNCHECK -> uncheck(commandRest[1]);
            case HELP -> consoleInputOutput.help();
            case DEADLINE -> deadline(commandRest[1]);
        }
    }


    private void show() {
        Collection<Project> projects = projectsStorage.getProjectsWithTasks().values();
        consoleInputOutput.showProjectsAndTasks(projects);
    }

    private void add(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommandString = subcommandRest[0];

        SubCommand
                .fromString(subcommandString)
                .ifPresentOrElse(
                        sc -> handleSubcommand(sc, subcommandRest[1]),
                        () -> consoleInputOutput.error(subcommandString)
                );
    }

    private void handleSubcommand(SubCommand sc, String rest) {
        switch (sc) {
            case PROJECT -> addProject(rest);
            case TASK -> {
                String[] projectTask = rest.split(" ", 2);
                addTask(projectTask[0], projectTask[1]);
            }
        }
    }


    private void addProject(String name) {
        projectsStorage.addProject(name);
    }

    private void addTask(String project, String taskDescription) {
        Optional<Project> updatedProject = projectsStorage.addTaskToProject(project, taskDescription);
        if (updatedProject.isEmpty()) {
            consoleInputOutput.printProjectNotFoundByName(project);
        }
    }

    private void check(String idString) {
        setDone(idString, true);
    }

    private void uncheck(String idString) {
        setDone(idString, false);
    }

    private void setDone(String idString, boolean done) {
        long id = Long.parseLong(idString);
        Optional<Task> updatedTask = projectsStorage.updateDoneValueInTask(id, done);
        if (updatedTask.isEmpty()) {
            consoleInputOutput.printTaskNotFoundById(id);
        }
    }

    private void deadline(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String idString = subcommandRest[0];
        String dateString = subcommandRest[1];

        long taskId = Long.parseLong(idString);
        LocalDate deadline = LocalDate.parse(dateString);

        Optional<Task> updatedTask =  projectsStorage.updateDeadlineValueInTask(taskId, deadline);
        if (updatedTask.isEmpty()) {
            consoleInputOutput.printTaskNotFoundById(taskId);
        }
    }
}
