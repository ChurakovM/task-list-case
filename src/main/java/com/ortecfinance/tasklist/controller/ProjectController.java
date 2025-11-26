package com.ortecfinance.tasklist.controller;

import com.ortecfinance.tasklist.dtos.ProjectDto;
import com.ortecfinance.tasklist.dtos.TaskDto;
import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;
import com.ortecfinance.tasklist.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ortecfinance.tasklist.utils.DeadlineUtils.getDeadlineLabel;
import static com.ortecfinance.tasklist.utils.DeadlineUtils.parseString;
import static com.ortecfinance.tasklist.utils.TasksDataUtils.groupTasksByProject;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final TaskService taskService;

    @PostMapping
    public String createProject(@RequestBody ProjectDto projectDto) {
        String name = projectDto.getName();
        taskService.addProject(name);
        return "Project created: " + name;
    }

    @GetMapping
    public Map<String, Project> getProjects() {
        return taskService.retrieveAllProjects();
    }

    @PostMapping("/{projectName}/tasks")
    public String createTaskForProject(
            @PathVariable("projectName") String projectName,
            @RequestBody TaskDto taskDto
    ) {
        taskService.addTask(projectName, taskDto.getDescription());
        return "Task added to project: " + projectName;
    }

    @PutMapping("/{projectName}/tasks/{taskId}")
    public String updateTaskDeadline(
            @PathVariable String projectName,
            @PathVariable long taskId,
            @RequestParam String deadline
    ) {
        taskService.updateDeadlineInTask(taskId, parseString((deadline)));
        return "Deadline updated for task ID: " + taskId;
    }

    @GetMapping("/view_by_deadline")
    public Map<String, Map<String, List<Task>>> getTasksByDeadline() {
        Map<Long, Task> allTasks = taskService.retrieveAllTasks();
        Map<LocalDate, List<Long>> deadlinesWithTaskIds = taskService.retrieveDeadlinesWithTaskIds();
        Set<Long> tasksWithoutDeadline = taskService.retrieveTasksWithoutDeadline();

        Map<String, Map<String, List<Task>>> result = new LinkedHashMap<>();

        // Tasks with deadlines
        for (Map.Entry<LocalDate, List<Long>> entry : deadlinesWithTaskIds.entrySet()) {
            String deadlineLabel = getDeadlineLabel(entry.getKey());
            Map<String, List<Task>> tasksByProject = groupTasksByProject(entry.getValue(), allTasks);
            result.put(deadlineLabel, tasksByProject);
        }

        // Tasks without deadlines
        if (!tasksWithoutDeadline.isEmpty()) {
            result.put("No deadline", groupTasksByProject(tasksWithoutDeadline, allTasks));
        }

        return result;
    }

}
