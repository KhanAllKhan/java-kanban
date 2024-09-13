package kz.yandex.taskTracker.service;
import kz.yandex.taskTracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.remove(0); // Удаляем самый старый элемент
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
