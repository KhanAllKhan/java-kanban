package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Task;
import kz.yandex.taskTracker.model.Epic;
import kz.yandex.taskTracker.model.Subtask;
import kz.yandex.taskTracker.model.Status;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    @Test
    public void testAddAndRemoveHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));

        historyManager.remove(task1.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void testPreventDuplicateHistoryEntries() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        historyManager.add(task);
        historyManager.add(task); // Добавление той же задачи второй раз

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testDataIntegrityAfterSubtaskRemoval() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic(1, "Epic 1", "Description 1");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, epic.getId());
        manager.addSubtask(subtask1);

        // Удаление подзадачи и проверка целостности данных
        manager.removeSubtask(subtask1.getId());
        assertNull(manager.getSubtask(subtask1.getId()));
        assertFalse(manager.getEpic(epic.getId()).getSubtaskIds().contains(subtask1.getId()));
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

        // Удаление эпика и проверка удаления подзадач из истории
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

        // Удаление задачи и проверка удаления из истории
        manager.removeTask(task.getId());

        List<Task> history = manager.getHistory();
        assertFalse(history.contains(task));
    }
}

