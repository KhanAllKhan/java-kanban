package kz.yandex.taskTracker.model;

import kz.yandex.taskTracker.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    private Duration duration;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW, Duration.ZERO, LocalDateTime.now());
        this.duration = Duration.ZERO;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtask(int subtaskId) {
        if (this.getId() == subtaskId) {
            throw new IllegalArgumentException("Epic cannot add itself as a subtask");
        }
        subtaskIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void calculateFields(List<Subtask> subtasks) {
        duration = Duration.ZERO;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (Subtask subtask : subtasks) {
            if (subtask != null) {
                if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                    duration = duration.plus(subtask.getDuration());
                    if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                        startTime = subtask.getStartTime();
                    }
                    LocalDateTime subtaskEndTime = subtask.getStartTime().plus(subtask.getDuration());
                    if (endTime == null || subtaskEndTime.isAfter(endTime)) {
                        endTime = subtaskEndTime;
                    }
                }
            }
        }
        this.endTime = endTime;

        System.out.println("Calculated duration: " + duration);
        System.out.println("Calculated startTime: " + startTime);
        System.out.println("Calculated endTime: " + endTime);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds) &&
                Objects.equals(duration, epic.duration) &&
                Objects.equals(startTime, epic.startTime) &&
                Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDescription() + "," + getDuration().toMinutes() + "," + getStartTime() + "," + getEndTime();
    }
}
