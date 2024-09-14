package kz.yandex.taskTracker.service;
import kz.yandex.taskTracker.model.Task;


import java.util.LinkedList;


public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();
    private final static int maxSizeHistory = 10;

    @Override
    public void add(Task task) {
        if (history.size() >= maxSizeHistory) {
            history.remove(0); // Удаляем самый старый элемент
        }
        history.add(task);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
