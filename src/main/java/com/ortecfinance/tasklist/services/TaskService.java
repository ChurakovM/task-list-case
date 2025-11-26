package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.console.ConsoleOutput;
import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;
import com.ortecfinance.tasklist.storage.ProjectsStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.ortecfinance.tasklist.utils.DeadlineUtils.parseString;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final ProjectsStorage projectsStorage;
    private final ConsoleOutput consoleOutput;

    public void showAllProjects() {
        Collection<Project> projects = projectsStorage.getAllProjects().values();
        consoleOutput.showProjectsAndTasks(projects);
    }

    public void addProject(String name) {
        projectsStorage.addProject(name);
    }

    public void addTask(String project, String taskDescription) {
        Optional<Project> updatedProject = projectsStorage.addTaskToProject(project, taskDescription);
        if (updatedProject.isEmpty()) {
            consoleOutput.printProjectNotFoundByName(project);
        }
    }

    public void checkTask(String idString) {
        updateDoneInTask(idString, true);
    }

    public void uncheckTask(String idString) {
        updateDoneInTask(idString, false);
    }

    private void updateDoneInTask(String idString, boolean done) {
        long id = Long.parseLong(idString);
        Optional<Task> updatedTask = projectsStorage.updateDoneValueInTask(id, done);
        if (updatedTask.isEmpty()) {
            consoleOutput.printTaskNotFoundById(id);
        }
    }

    public void updateDeadlineInTask(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String idString = subcommandRest[0];
        String dateString = subcommandRest[1];

        long taskId = Long.parseLong(idString);
        LocalDate deadline = parseString(dateString);

        Optional<Task> updatedTask =  projectsStorage.updateDeadlineValueInTask(taskId, deadline);
        if (updatedTask.isEmpty()) {
            consoleOutput.printTaskNotFoundById(taskId);
        }
    }

    public void viewTasksForToday() {
        consoleOutput.showProjectsAndTasksForToday();
    }

    public void viewTasksByDeadline() {
        consoleOutput.showProjectsAndTasksBasedOnDeadline();
    }
}
