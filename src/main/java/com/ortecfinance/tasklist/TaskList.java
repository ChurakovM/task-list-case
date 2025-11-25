package com.ortecfinance.tasklist;

import com.ortecfinance.tasklist.commands.Commands;
import com.ortecfinance.tasklist.commands.SubCommand;
import com.ortecfinance.tasklist.console.ConsoleOutput;
import com.ortecfinance.tasklist.services.TaskService;
import com.ortecfinance.tasklist.storage.ProjectsStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

@Service
@RequiredArgsConstructor
public final class TaskList implements Runnable {

    private static final String QUIT = "quit";
    private final TaskService taskService;
    private final ConsoleOutput consoleOutput;
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private PrintWriter out = new PrintWriter(System.out);

    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        new TaskList(in, out).run();
    }

    public TaskList(BufferedReader reader, PrintWriter writer) {
        this.in = reader;
        this.out = writer;
        consoleOutput = new ConsoleOutput(out);
        ProjectsStorage projectsStorage = new ProjectsStorage();
        taskService = new TaskService(projectsStorage, consoleOutput);
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
                        () -> consoleOutput.error(commandString)
                );
    }

    private void handleCommand(Commands cmd, String[] commandRest) {
        switch (cmd) {
            case SHOW -> taskService.showAllProjects();
            case ADD -> add(commandRest[1]);
            case CHECK -> taskService.checkTask(commandRest[1]);
            case UNCHECK -> taskService.uncheckTask(commandRest[1]);
            case HELP -> consoleOutput.help();
            case DEADLINE -> taskService.updateDeadlineInTask(commandRest[1]);
            case TODAY -> taskService.viewTasksForToday();
            case VIEW_BY_DEADLINE -> taskService.viewTasksByDeadline();
        }
    }

    private void add(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommandString = subcommandRest[0];

        SubCommand
                .fromString(subcommandString)
                .ifPresentOrElse(
                        sc -> handleSubcommand(sc, subcommandRest[1]),
                        () -> consoleOutput.error(subcommandString)
                );
    }

    private void handleSubcommand(SubCommand sc, String rest) {
        switch (sc) {
            case PROJECT -> taskService.addProject(rest);
            case TASK -> {
                String[] projectTask = rest.split(" ", 2);
                taskService.addTask(projectTask[0], projectTask[1]);
            }
        }
    }
}
