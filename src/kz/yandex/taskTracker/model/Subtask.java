package kz.yandex.taskTracker.model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        if (id == epicId) {
            throw new IllegalArgumentException("Подзадача не может быть отдельной эпиком");
        }
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (this.getId() == epicId) {
            throw new IllegalArgumentException("Подзадача не может быть отдельной эпопиком");
        }
        this.epicId = epicId;
    }
}
