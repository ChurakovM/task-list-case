package com.ortecfinance.tasklist.console;

import com.ortecfinance.tasklist.commands.Commands;
import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

import static com.ortecfinance.tasklist.utils.DeadlineUtils.getDeadlineLabel;
import static com.ortecfinance.tasklist.utils.TasksDataUtils.groupTasksByProject;

@Component
public class ConsoleOutput {

    private final PrintWriter out;

    @Autowired
    public ConsoleOutput(PrintWriter out) {
        this.out = out;
    }

    public void help() {
        out.println("Commands:");
        for (Commands cmd : Commands.values()) {
            out.println("  " + cmd.getHelpText());
        }
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
                String deadlineLabel = getDeadlineLabel(task.getDeadline());
                out.printf(
                        "    [%c] %d: %s (deadline: %s)%n",
                        (task.isDone() ? 'x' : ' '),
                        task.getId(),
                        task.getDescription(),
                        deadlineLabel
                );
            }
            out.println();
        }
    }

    public void showProjectsAndTasksForToday(Map<Long, Task> allTasks,
                                             Map<LocalDate, List<Long>> deadlinesWithTaskIds) {

        LocalDate today = LocalDate.now();

        // Check if there are tasks with today's deadline
        List<Long> taskIdsForToday = deadlinesWithTaskIds.get(today);
        if (taskIdsForToday == null || taskIdsForToday.isEmpty()) {
            out.println("No tasks for today.");
            return;
        }

        Map<String, List<Task>> tasksByProject = groupTasksByProject(taskIdsForToday, allTasks);
        printTasksByProject(tasksByProject);
    }

    public void showProjectsAndTasksBasedOnDeadline(Map<Long, Task> allTasks,
                                                    Map<LocalDate, List<Long>> deadlinesWithTaskIds,
                                                    Set<Long> tasksWithoutDeadline) {
        if (allTasks == null || allTasks.isEmpty()) {
            out.println("No tasks in the system.");
            return;
        }

        // Print all deadlines first
        for (Map.Entry<LocalDate, List<Long>> entry : deadlinesWithTaskIds.entrySet()) {
            LocalDate deadline = entry.getKey();
            List<Long> taskIds = entry.getValue();

            out.println(getDeadlineLabel(deadline));

            Map<String, List<Task>> tasksByProject = groupTasksByProject(taskIds, allTasks);
            printTasksByProject(tasksByProject);
        }

        // Print the rest of things without a deadline
        if (!tasksWithoutDeadline.isEmpty()) {
            out.println("No deadline:");

            Map<String, List<Task>> tasksByProject = groupTasksByProject(tasksWithoutDeadline, allTasks);
            printTasksByProject(tasksByProject);
        }
    }

    private void printTasksByProject(Map<String, List<Task>> tasksByProject) {
        for (Map.Entry<String, List<Task>> projectEntry : tasksByProject.entrySet()) {
            out.println("    " + projectEntry.getKey() + ":");
            for (Task task : projectEntry.getValue()) {
                out.printf("        %d: %s%n", task.getId(), task.getDescription());
            }
        }
    }
}
