package kz.yandex.taskTracker;

import kz.yandex.taskTracker.model.*;
import kz.yandex.taskTracker.service.FileBackedTaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Файл, в котором сохраняются и загружаются задачи
        File file = new File("tasks.csv");

        // Загружаем задачи из файла или создаём новый FileBackedTaskManager, если файла нет
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(file);

        // Создаём эпик (без статуса)
        Epic epic = new Epic(1, "Эпик задача", "Описание эпика");
        taskManager.addEpic(epic);

        // Создаём подзадачу, привязанную к эпику
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask1);

        // Можно добавить больше подзадач, если нужно
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, epic.getId());
        taskManager.addSubtask(subtask2);

        // Создаём простую задачу (не эпик и не подзадачу)
        Task task = new Task(4, "Обычная задача", "Описание обычной задачи", Status.DONE);
        taskManager.addTask(task);

        // Сохраняем все задачи в файл
        taskManager.save();

        // Можно добавить вывод для проверки добавленных задач
        System.out.println("Задачи успешно добавлены и сохранены.");
    }
}
