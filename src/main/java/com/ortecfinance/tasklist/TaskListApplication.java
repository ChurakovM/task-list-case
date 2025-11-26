package com.ortecfinance.tasklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskListApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TaskListApplication.class);
        var context = app.run(args);

        if (args.length == 0) {
            System.out.println("Starting console application...");
            TaskList taskList = context.getBean(TaskList.class);
            taskList.run();
        } else {
            System.out.println("API started at http://localhost:8080/tasks");
        }
    }
}
