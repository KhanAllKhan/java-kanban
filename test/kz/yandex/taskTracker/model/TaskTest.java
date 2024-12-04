package kz.yandex.taskTracker.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void testTasksEquality() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        assertEquals(task1, task2);
    }

    @Test
    public void testTasksInequality() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task 2", "Description 2", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        assertNotEquals(task1, task2);
    }

    @Test
    public void testEpicsEquality() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1");
        Epic epic2 = new Epic(1, "Epic 1", "Description 1");

        assertEquals(epic1, epic2);
    }

    @Test
    public void testEpicsInequality() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1");
        Epic epic2 = new Epic(2, "Epic 2", "Description 2");

        assertNotEquals(epic1, epic2);
    }
}
