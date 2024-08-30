package kz.yandex.taskTracker.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}



