package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER = "id,type,name,status,description,duration,startTime,endTime,epic\n";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            for (Task task : getTasks()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(epic.toString() + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving tasks", e);
        }
    }

    public void calculateFields(Epic epic, List<Subtask> subtasks) {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (Subtask subtask : subtasks) {
            if (subtask != null) {
                Duration subtaskDuration = subtask.getDuration();
                LocalDateTime subtaskStartTime = subtask.getStartTime();

                if (subtaskStartTime != null && subtaskDuration != null) {
                    totalDuration = totalDuration.plus(subtaskDuration);

                    if (startTime == null || subtaskStartTime.isBefore(startTime)) {
                        startTime = subtaskStartTime;
                    }

                    LocalDateTime subtaskEndTime = subtaskStartTime.plus(subtaskDuration);

                    if (endTime == null || subtaskEndTime.isAfter(endTime)) {
                        endTime = subtaskEndTime;
                    }
                }
            }
        }

        epic.setStartTime(startTime);
        epic.setDuration(totalDuration);
        epic.setEndTime(endTime);

        System.out.println("Calculated total duration: " + totalDuration);
        System.out.println("Calculated startTime: " + startTime);
        System.out.println("Calculated endTime: " + endTime);
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Пропустить заголовок
            while ((line = br.readLine()) != null) {
                Task task = Task.fromString(line);
                addTaskToManager(manager, task);
            }
            manager.initializeEpicSubtasks();
            manager.updateIdCounter();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задачи", e);
        }
        return manager;
    }

    private static void addTaskToManager(FileBackedTaskManager manager, Task task) {
        switch (task.getType()) {
            case TASK:
                manager.tasks.put(task.getId(), task);
                break;
            case EPIC:
                Epic epic = (Epic) task;
                manager.epics.put(epic.getId(), epic);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                manager.subtasks.put(subtask.getId(), subtask);
                break;
        }
    }

    private void initializeEpicSubtasks() {
        for (Epic epic : epics.values()) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    epic.addSubtask(subtaskId);
                }
            }
            // Рассчитываем поля duration и startTime для эпиков
            calculateFields(epic, getEpicSubtasks(epic.getId()));
        }
    }

    private void updateIdCounter() {
        int maxId = 0;
        maxId = Math.max(maxId, findMaxId(tasks.values()));
        maxId = Math.max(maxId, findMaxId(epics.values()));
        maxId = Math.max(maxId, findMaxId(subtasks.values()));
        setIdCounter(maxId);
    }

    private <T extends Task> int findMaxId(Iterable<T> tasks) {
        int maxId = 0;
        for (T task : tasks) {
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
        }
        return maxId;
    }
}
