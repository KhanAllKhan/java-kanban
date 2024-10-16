package kz.yandex.taskTracker.model;

import kz.yandex.taskTracker.service.TaskType;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;
    private TaskType type;

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public TaskType getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (id != 0) {
            hash = hash + id;
        }
        hash = hash * 31;
        return hash;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", id, type, name, status, description);
    }

    public static Task fromString(String value) {
        String[] taskData = value.split(",");
        int id = Integer.parseInt(taskData[0]);
        TaskType type = TaskType.valueOf(taskData[1]);
        String name = taskData[2];
        Status status = Status.valueOf(taskData[3]);
        String description = taskData[4];
        return new Task(id, name, description, status);
    }
}
