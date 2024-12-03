package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Epic;
import kz.yandex.taskTracker.model.Subtask;
import kz.yandex.taskTracker.model.Task;
import kz.yandex.taskTracker.model.Status;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    public void testAddAndRemoveHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(2, "Task 2", "Description 2", Status.IN_PROGRESS);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));

        historyManager.remove(task1.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));

        historyManager.remove(task2.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testDuplicateTaskInHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        historyManager.add(task1);
        historyManager.add(task1); // Добавление той же задачи второй раз

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testHistoryOrder() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(2, "Task 2", "Description 2", Status.IN_PROGRESS);
        Task task3 = new Task(3, "Task 3", "Description 3", Status.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    public void testRemoveEpicAndRelatedSubtasksFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(1, "Epic 1", "Description 1");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2", Status.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        // Удаление эпика
        manager.removeEpic(epic.getId());

        List<Task> history = manager.getHistory();
        assertFalse(history.contains(epic));
        assertFalse(history.contains(subtask1));
        assertFalse(history.contains(subtask2));
    }

    @Test
    public void testRemoveTaskFromHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        manager.addTask(task);

        // Удаление задачи
        manager.removeTask(task.getId());

        List<Task> history = manager.getHistory();
        assertFalse(history.contains(task));
    }

    @Test
    public void testDataIntegrityAfterSubtaskRemoval() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic(1, "Epic 1", "Description 1");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, epic.getId());
        manager.addSubtask(subtask1);

        // Удаление подзадачи
        manager.removeSubtask(subtask1.getId());

        assertNull(manager.getSubtask(subtask1.getId()));
        assertFalse(manager.getEpic(epic.getId()).getSubtaskIds().contains(subtask1.getId()));
    }

    @Test
    public void testEpicSubtaskIntegrityAfterSubtaskUpdates() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic(1, "Epic 1", "Description 1");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2", Status.NEW, epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        // Удаление подзадачи
        manager.removeSubtask(subtask1.getId());
        assertFalse(manager.getEpic(epic.getId()).getSubtaskIds().contains(subtask1.getId()));
        assertTrue(manager.getEpic(epic.getId()).getSubtaskIds().contains(subtask2.getId()));
    }


}
