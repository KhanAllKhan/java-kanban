package kz.yandex.taskTracker.service;

import java.io.File;

public class Managers {
    private Managers() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedManager() {
        return new FileBackedTaskManager(new File("tasks.csv"));
    }
}
