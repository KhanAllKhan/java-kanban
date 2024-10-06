package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();
    private final static int MAX_SIZE_HISTORY = 10;

    @Override
    public void remove(int id) {

    }

    @Override
    public void add(Task task) {
        if (history.size() < MAX_SIZE_HISTORY) {
            history.add(task);
        }else {
            history.remove(0);// Удаляем самый старый элемент
        }

    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
