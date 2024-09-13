package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Task;
import kz.yandex.taskTracker.model.Epic;
import kz.yandex.taskTracker.model.Subtask;
import kz.yandex.taskTracker.model.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    public void testAddAndFindTasksById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        Epic epic = new Epic(2, "Epic 1", "Description 1");
        Subtask subtask = new Subtask(3, "Subtask 1", "Description 1", Status.NEW, epic.getId());

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        assertEquals(task, manager.getTask(task.getId()));
        assertEquals(epic, manager.getEpic(epic.getId()));
        assertEquals(subtask, manager.getSubtask(subtask.getId()));
    }

    @Test
    public void testTaskIdConflict() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(0, "Task 2", "Description 2", Status.NEW);

        manager.addTask(task1);
        manager.addTask(task2);

        assertEquals(task1, manager.getTask(1));
        assertNotNull(manager.getTask(task2.getId()));
    }

    @Test
    public void testTaskImmutability() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        manager.addTask(task);

        Task retrievedTask = manager.getTask(task.getId());
        assertEquals(task, retrievedTask);
        assertEquals(task.getName(), retrievedTask.getName());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }
}


