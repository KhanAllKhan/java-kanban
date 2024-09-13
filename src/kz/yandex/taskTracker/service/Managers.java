package kz.yandex.taskTracker.service;

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
}
