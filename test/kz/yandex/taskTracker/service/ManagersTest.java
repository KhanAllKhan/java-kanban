package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testNoConflict() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 10, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getTasks().size());
    }

    @Test
    public void testStartBeforeEndConflict() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 10, 0));
        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }

    @Test
    public void testStartAtSameTime() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 9, 0));
        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }


    @Test
    public void testEndAfterStartConflict() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 8, 30));
        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }

    @Test
    public void testCompleteOverlap() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 9, 30));
        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }

    @Test
    public void testNoOverlap() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 10, 0));
        Task task3 = new Task(3, "Task 3", "Description 3", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 11, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        assertEquals(3, taskManager.getTasks().size());
    }

    @Test
    public void testSameStartAndDuration() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 9, 0));
        taskManager.addTask(task1);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }
}
