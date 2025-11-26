package com.ortecfinance.tasklist.services;

import com.ortecfinance.tasklist.console.ConsoleOutput;
import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;
import com.ortecfinance.tasklist.storage.DeadlineCache;
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
    private final DeadlineCache deadlineCache;

    public void showAllProjects() {
        consoleOutput.showProjectsAndTasks();
    }

    public Map<String, Project> retrieveAllProjects() {
        return projectsStorage.getAllProjects();
    }

    public Map<Long, Task> retrieveAllTasks() {
        return projectsStorage.getAllTasks();
    }

    public Map<LocalDate, List<Long>> retrieveDeadlinesWithTaskIds() {
        return deadlineCache.getDeadlinesWithTaskIdsCache();
    }

    public Set<Long> retrieveTasksWithoutDeadline() {
        return deadlineCache.getAllTasksIdsWithoutDeadlinesCache();
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

        updateDeadlineInTask(taskId, deadline);
    }

    public void updateDeadlineInTask(long taskId, LocalDate deadline) {
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
