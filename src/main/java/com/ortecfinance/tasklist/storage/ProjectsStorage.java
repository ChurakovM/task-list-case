package com.ortecfinance.tasklist.storage;

import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

public class ProjectsStorage {

    @Getter
    private final Map<String, Project> projectsWithTasks = new LinkedHashMap<>();
    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    private long lastTaskId = 0;

    public void addProject(String name) {
        Project newProject = new Project(name, new ArrayList<>());
        projectsWithTasks.put(name, newProject);
    }

    private Optional<Project> getProjectByName(String name) {
        if (projectsWithTasks.containsKey(name)) {
            return Optional.of(projectsWithTasks.get(name));
        }
        return Optional.empty();
    }

    /***
     * Method to add a task to an existing project.
     * @param projectName the name of the project.
     * @param taskDescription the description of the task that has to be put in.
     * @return an updated project wrapped in Optional. If the project is not found, empty Optional is returned.
     */
    public Optional<Project> addTaskToProject(String projectName, String taskDescription) {
        Optional<Project> optionalProject = getProjectByName(projectName);

        if (optionalProject.isEmpty()) {
            return optionalProject;
        }

        Project foundProject = optionalProject.get();

        long taskId = nextTaskId();
        Task newTask = new Task(taskId, taskDescription, false);
        foundProject.addTask(newTask);

        tasks.put(taskId, newTask);

        return optionalProject;
    }

    private Optional<Task> getTaskById(long taskId) {
        if (tasks.containsKey(taskId)) {
            return Optional.of(tasks.get(taskId));
        }
        return Optional.empty();
    }

    /***
     * Method to change "done" property in a task.
     * @param taskId the ID of the ask that has to be updated.
     * @param done the value that has to be put into a task.
     * @return an update task wrapped in Optional. If the task is not found, empty Optional is returned.
     */
    public Optional<Task> updateDoneValueInTask(long taskId, boolean done) {
        Optional<Task> optionalTask = getTaskById(taskId);
        optionalTask.ifPresent(task -> task.setDone(done));
        return optionalTask;
    }

    /***
     * Method to change "deadline" property in a task.
     * @param taskId the ID of the ask that has to be updated.
     * @param deadline the value that has to be put into a task.
     * @return an update task wrapped in Optional. If the task is not found, empty Optional is returned.
     */
    public Optional<Task> updateDeadlineValueInTask(long taskId, LocalDate deadline) {
        Optional<Task> optionalTask = getTaskById(taskId);
        optionalTask.ifPresent(task -> task.setDeadline(deadline));
        return optionalTask;
    }

    private long nextTaskId() {
        return ++lastTaskId;
    }
}
