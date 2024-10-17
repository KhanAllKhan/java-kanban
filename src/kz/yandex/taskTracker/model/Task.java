package kz.yandex.taskTracker.model;

import kz.yandex.taskTracker.service.TaskType;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");

        // Убедимся, что в массиве достаточно полей
        if (fields.length < 4) {
            throw new IllegalArgumentException("Invalid input string: " + value);
        }

        int id = Integer.parseInt(fields[0].trim());
        TaskType type = TaskType.valueOf(fields[1].trim().toUpperCase()); // Приводим к верхнему регистру
        String name = fields[2].trim();
        Status status = Status.valueOf(fields[3].trim().toUpperCase()); // Приводим к верхнему регистру
        String description = fields.length > 4 ? fields[4].trim() : ""; // Если описание не передано, устанавливаем пустую строку

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                if (fields.length < 6) { // Проверяем, что есть epicId
                    throw new IllegalArgumentException("Subtask requires epicId: " + value);
                }
                int epicId = Integer.parseInt(fields[5].trim()); // Извлекаем epicId из шестого поля
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return id + "," + getType() + "," + name + "," + status + "," + description;
    }
}
