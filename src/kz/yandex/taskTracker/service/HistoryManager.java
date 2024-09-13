package kz.yandex.taskTracker.service;
import kz.yandex.taskTracker.model.Task;
import java.util.List;
public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
