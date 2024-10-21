package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.*;

import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER = "id,type,name,status,description,epic\n";
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

    @Override
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
        }
        save();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        save();
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        save();
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
                updateEpicStatus(epic);
            }
        }
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Пропустить заголовок
            while ((line = br.readLine()) != null) {
                Task task = Task.fromString(line);
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
                        Epic parentEpic = manager.epics.get(subtask.getEpicId());
                        if (parentEpic != null) {
                            parentEpic.addSubtask(subtask.getId());
                        }
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задачи", e);
        }
        return manager;
    }
}
