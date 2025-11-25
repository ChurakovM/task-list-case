package com.ortecfinance.tasklist.storage;

import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

public class ProjectsStorage {

    @Getter
    private final Map<String, Project> allProjects = new LinkedHashMap<>();

    @Getter
    private final Map<Long, Task> allTasks = new LinkedHashMap<>();

    @Getter
    private final Map<LocalDate, List<Long>> deadlinesWithTaskIdsCache = new TreeMap<>();

    @Getter
    private final Set<Long> allTasksIdsWithoutDeadlinesCache = new LinkedHashSet<>();

    private long lastTaskId = 0;

    public void addProject(String name) {
        Project newProject = new Project(name, new ArrayList<>());
        allProjects.put(name, newProject);
    }

    private Optional<Project> getProjectByName(String name) {
        return Optional.ofNullable(allProjects.get(name));
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
        Task newTask = new Task(taskId, taskDescription, false, null, foundProject);
        foundProject.addTask(newTask);

        allTasks.put(taskId, newTask);
        allTasksIdsWithoutDeadlinesCache.add(taskId);

        return optionalProject;
    }

    /***
     * Method to get a Task object by its ID.
     * @param taskId the required ID to find a task.
     * @return a task wrapped in Optional. If the task is not found, empty Optional is returned.
     */
    private Optional<Task> getTaskById(long taskId) {
        return Optional.ofNullable(allTasks.get(taskId));
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
     * Method to change "newDeadline" property in a task.
     * @param taskId the ID of the ask that has to be updated.
     * @param newDeadline the value that has to be put into a task.
     * @return an update task wrapped in Optional. If the task is not found, empty Optional is returned.
     */
    public Optional<Task> updateDeadlineValueInTask(long taskId, LocalDate newDeadline) {
        Optional<Task> optionalTask = getTaskById(taskId);

        optionalTask.ifPresent(task -> {
            LocalDate oldDeadline = task.getDeadline();
            task.setDeadline(newDeadline);

            // Remove from old deadline cache if it exists
            if (oldDeadline != null) {
                removeTaskIdFromDeadlineCache(taskId, oldDeadline);
            } else {
                allTasksIdsWithoutDeadlinesCache.remove(taskId); // previously without deadline
            }

            // Add to new deadline cache or "no deadline" list
            if (newDeadline != null) {
                List<Long> tasks = deadlinesWithTaskIdsCache.getOrDefault(newDeadline, new ArrayList<>());
                tasks.add(taskId);
                deadlinesWithTaskIdsCache.put(newDeadline, tasks);
            } else {
                allTasksIdsWithoutDeadlinesCache.add(taskId);
            }
        });

        return optionalTask;
    }


    private void removeTaskIdFromDeadlineCache(long taskId, LocalDate oldDeadline) {
        List<Long> oldList = deadlinesWithTaskIdsCache.get(oldDeadline);
        if (oldList != null) {
            oldList.remove(taskId);
            if (oldList.isEmpty()) {
                deadlinesWithTaskIdsCache.remove(oldDeadline);
            }
        }
    }

    private long nextTaskId() {
        return ++lastTaskId;
    }
}
