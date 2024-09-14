package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Task;
import kz.yandex.taskTracker.model.Status;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.LinkedList;

public class HistoryManagerTest {
    @Test
    public void testHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task(1, "Task 1", "Description 1", Status.NEW);
        historyManager.add(task);

        LinkedList<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testHistorySizeLimit() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        // Добавляем 10 задач в историю
        for (int i = 1; i <= 10; i++) {
            Task task = new Task(i, "Task " + i, "Description " + i, Status.NEW);
            historyManager.add(task);
        }

        // Проверяем, что размер истории равен 10
        LinkedList<Task> history = historyManager.getHistory();
        assertEquals(10, history.size());

        // Добавляем еще одну задачу, чтобы проверить удаление старых элементов
        Task newTask = new Task(11, "Task 11", "Description 11", Status.NEW);
        historyManager.add(newTask);

        // Проверяем, что размер истории все еще равен 10
        history = historyManager.getHistory();
        assertEquals(10, history.size());

        // Проверяем, что самая старая задача была удалена
        assertFalse(history.contains(new Task(1, "Task 1", "Description 1", Status.NEW)));
        assertTrue(history.contains(newTask));
    }
}
