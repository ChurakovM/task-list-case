package com.ortecfinance.tasklist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ortecfinance.tasklist.controller.ProjectController;
import com.ortecfinance.tasklist.dtos.ProjectDto;
import com.ortecfinance.tasklist.dtos.TaskDto;
import com.ortecfinance.tasklist.models.Project;
import com.ortecfinance.tasklist.models.Task;
import com.ortecfinance.tasklist.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.ortecfinance.tasklist.utils.DeadlineUtils.DEFAULT_DATE_FORMAT;
import static com.ortecfinance.tasklist.utils.DeadlineUtils.getDeadlineLabel;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        reset(taskService);
    }

    @Test
    void shouldCreateProject() throws Exception {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Secrets");

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Project created: Secrets"));

        verify(taskService).addProject("Secrets");
    }

    @Test
    void shouldReturnAllProjects() throws Exception {
        Project project = new Project("Secrets", new ArrayList<>());
        Map<String, Project> projects = Map.of("Secrets", project);

        when(taskService.retrieveAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Secrets.name", is("Secrets")));
    }

    @Test
    void shouldCreateTaskForProject() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setDescription("Eat more donuts");

        mockMvc.perform(post("/projects/Secrets/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task added to project: Secrets"));

        verify(taskService).addTask("Secrets", "Eat more donuts");
    }

    @Test
    void shouldUpdateTaskDeadline() throws Exception {
        LocalDate deadline = LocalDate.now();
        String deadlineStr = deadline.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));

        mockMvc.perform(put("/projects/Secrets/tasks/1")
                .param("deadline", deadlineStr))
                .andExpect(status().isOk())
                .andExpect(content().string("Deadline updated for task ID: 1"));

        verify(taskService).updateDeadlineInTask(1, deadline);
    }

    @Test
    void shouldReturnTasksGroupedByDeadline() throws Exception {
        Task task1 = new Task(1L, "Eat more donuts", false, LocalDate.now(), "Secrets");
        Task task2 = new Task(2L, "Destroy all humans", false, null, "Secrets");

        Map<Long, Task> allTasks = Map.of(
                1L, task1,
                2L, task2
        );

        Map<LocalDate, List<Long>> deadlinesWithTaskIds = Map.of(
                LocalDate.now(), List.of(1L)
        );

        Set<Long> tasksWithoutDeadline = Set.of(2L);

        when(taskService.retrieveAllTasks()).thenReturn(allTasks);
        when(taskService.retrieveDeadlinesWithTaskIds()).thenReturn(deadlinesWithTaskIds);
        when(taskService.retrieveTasksWithoutDeadline()).thenReturn(tasksWithoutDeadline);

        mockMvc.perform(get("/projects/view_by_deadline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['" + getDeadlineLabel(LocalDate.now()) + "'].Secrets[0].description", is("Eat more donuts")))
                .andExpect(jsonPath("$.['No deadline'].Secrets[0].description", is("Destroy all humans")));
    }
}
