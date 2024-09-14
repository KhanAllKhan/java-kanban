package kz.yandex.taskTracker.service;
import kz.yandex.taskTracker.model.Task;
import java.util.LinkedList;
public interface HistoryManager {
    void add(Task task);
    LinkedList<Task> getHistory();
}
