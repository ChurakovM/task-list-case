package com.ortecfinance.tasklist.storage;

import com.ortecfinance.tasklist.models.Task;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Getter
public class DeadlineCache {

    private final Map<LocalDate, List<Long>> deadlinesWithTaskIdsCache = new TreeMap<>();
    private final Set<Long> allTasksIdsWithoutDeadlinesCache = new LinkedHashSet<>();

    public void addNewTaskIdWithoutDeadline(long taskId) {
        allTasksIdsWithoutDeadlinesCache.add(taskId);
    }

    public void updateCacheBasedOnDeadlineValue(Task task, LocalDate newDeadline) {
        long taskId = task.getId();
        LocalDate oldDeadline = task.getDeadline();

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
}
