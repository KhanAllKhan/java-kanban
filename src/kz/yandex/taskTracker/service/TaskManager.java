package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Epic;
import kz.yandex.taskTracker.model.Subtask;
import kz.yandex.taskTracker.model.Task;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    Task getTask(int id);

    void updateTask(Task task);

    void removeTask(int id);

    void removeAllTasks();

    void addSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    void updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    void removeAllSubtasks();

    void addEpic(Epic epic);

    Epic getEpic(int id);

    void updateEpic(Epic epic);

    void removeEpic(int id);

    void removeAllEpics();

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
    List<Task> getPrioritizedTasks();

}
