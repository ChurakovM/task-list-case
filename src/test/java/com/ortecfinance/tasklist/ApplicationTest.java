package com.ortecfinance.tasklist;

import com.ortecfinance.tasklist.console.ConsoleOutput;
import com.ortecfinance.tasklist.services.TaskService;
import com.ortecfinance.tasklist.storage.DeadlineCache;
import com.ortecfinance.tasklist.storage.ProjectsStorage;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.ortecfinance.tasklist.utils.DeadlineUtils.DEFAULT_DATE_FORMAT;
import static com.ortecfinance.tasklist.utils.DeadlineUtils.getDeadlineLabel;
import static java.lang.System.lineSeparator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ApplicationTest {
    public static final String PROMPT = "> ";
    private final PipedOutputStream inStream = new PipedOutputStream();
    private final PrintWriter inWriter = new PrintWriter(inStream, true);

    private final PipedInputStream outStream = new PipedInputStream();
    private final BufferedReader outReader = new BufferedReader(new InputStreamReader(outStream));

    private final Thread applicationThread;

    public ApplicationTest() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new PipedInputStream(inStream)));
        PrintWriter out = new PrintWriter(new PipedOutputStream(outStream), true);

        DeadlineCache deadlineCache = new DeadlineCache();
        ProjectsStorage storage = new ProjectsStorage(deadlineCache);
        ConsoleOutput consoleOutput = new ConsoleOutput(out, storage, deadlineCache);
        TaskService taskService = new TaskService(storage, consoleOutput, deadlineCache);

        TaskList taskList = new TaskList(taskService, consoleOutput, in, out);
        applicationThread = new Thread(taskList);
    }

    @BeforeEach
    public void start_the_application() throws IOException {
        applicationThread.start();
        readLines("Welcome to TaskList! Type 'help' for available commands.");
    }

    @AfterEach
    public void kill_the_application() throws IOException, InterruptedException {
        if (!stillRunning()) {
            return;
        }

        Thread.sleep(1000);
        if (!stillRunning()) {
            return;
        }

        applicationThread.interrupt();
        throw new IllegalStateException("The application is still running.");
    }

    @Test()
    void it_works() throws IOException {
        execute("show");

        execute("add project secrets");
        execute("add task secrets Eat more donuts.");
        execute("add task secrets Destroy all humans.");

        execute("show");
        readLines(
            "secrets",
            "    [ ] 1: Eat more donuts. (deadline: no deadline)",
            "    [ ] 2: Destroy all humans. (deadline: no deadline)",
            ""
        );

        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("add task training Coupling and Cohesion");
        execute("add task training Primitive Obsession");
        execute("add task training Outside-In TDD");
        execute("add task training Interaction-Driven Design");

        execute("check 1");
        execute("check 3");
        execute("check 5");
        execute("check 6");

        execute("show");
        readLines(
                "secrets",
                "    [x] 1: Eat more donuts. (deadline: no deadline)",
                "    [ ] 2: Destroy all humans. (deadline: no deadline)",
                "",
                "training",
                "    [x] 3: Four Elements of Simple Design (deadline: no deadline)",
                "    [ ] 4: SOLID (deadline: no deadline)",
                "    [x] 5: Coupling and Cohesion (deadline: no deadline)",
                "    [x] 6: Primitive Obsession (deadline: no deadline)",
                "    [ ] 7: Outside-In TDD (deadline: no deadline)",
                "    [ ] 8: Interaction-Driven Design (deadline: no deadline)",
                ""
        );

        execute("quit");
    }

    @Test
    void it_works_with_deadlines() throws IOException {
        execute("add project secrets");
        execute("add task secrets Eat more donuts.");
        execute("add task secrets Destroy all humans.");

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
        execute("deadline 1 " + today);
        String expectedDate2 = getDeadlineLabel(LocalDate.now().plusDays(1));
        execute("deadline 2 " + expectedDate2);

        execute("show");
        readLines(
                "secrets",
                "    [ ] 1: Eat more donuts. (deadline: today)",
                String.format("    [ ] 2: Destroy all humans. (deadline: %s)", expectedDate2),
                ""
        );

        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");

        String expectedDate3 = getDeadlineLabel(LocalDate.now().plusDays(2));
        execute("deadline 3 " + expectedDate3);

        execute("view-by-deadline");
        readLines(
                "today",
                "    secrets:",
                "        1: Eat more donuts.",
                "",
                expectedDate2,
                "    secrets:",
                "        2: Destroy all humans.",
                "",
                expectedDate3,
                "    training:",
                "        3: Four Elements of Simple Design",
                "",
                "No deadline:",
                "    training:",
                "        4: SOLID"
        );

        execute("quit");
    }

    private void execute(String command) throws IOException {
        read(PROMPT);
        write(command);
    }

    private void read(String expectedOutput) throws IOException {
        int length = expectedOutput.length();
        char[] buffer = new char[length];
        outReader.read(buffer, 0, length);
        String actualOutput = String.valueOf(buffer);
        assertThat(actualOutput, is(expectedOutput));
    }

    private void readLines(String... expectedOutput) throws IOException {
        for (String line : expectedOutput) {
            read(line + lineSeparator());
        }
    }

    private void write(String input) {
        inWriter.println(input);
    }

    private boolean stillRunning() {
        return applicationThread != null && applicationThread.isAlive();
    }
}
