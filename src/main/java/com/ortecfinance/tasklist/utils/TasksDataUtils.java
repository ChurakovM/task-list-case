package com.ortecfinance.tasklist.utils;

import com.ortecfinance.tasklist.models.Task;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class TasksDataUtils {

    public static Map<String, List<Task>> groupTasksByProject(Collection<Long> taskIds, Map<Long, Task> allTasks) {
        Map<String, List<Task>> tasksByProject = new LinkedHashMap<>();

        for (Long taskId : taskIds) {
            Task task = allTasks.get(taskId);
            if (task != null) {
                tasksByProject
                        .computeIfAbsent(task.getProjectName(), k -> new ArrayList<>())
                        .add(task);
            }
        }

        return tasksByProject;
    }
}
