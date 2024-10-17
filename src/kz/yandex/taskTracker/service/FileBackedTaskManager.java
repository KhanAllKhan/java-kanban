package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.*;

import java.io.*;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
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
                switch (task.getType()) {
                    case TASK:
                        manager.addTask(task);
                        break;
                    case EPIC:
                        manager.addEpic((Epic) task);
                        break;
                    case SUBTASK:
                        manager.addSubtask((Subtask) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задачи", e);
        }
        return manager;
    }
}
