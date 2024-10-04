package kz.yandex.taskTracker;

import kz.yandex.taskTracker.model.Epic;
import kz.yandex.taskTracker.model.Status;
import kz.yandex.taskTracker.model.Subtask;
import kz.yandex.taskTracker.model.Task;
import kz.yandex.taskTracker.service.Managers;
import kz.yandex.taskTracker.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создание задач
        Task task1 = new Task(0, "Переезд", "Собрать вещи", Status.NEW);
        Task task2 = new Task(0, "Собрать коробки", "Упаковать вещи", Status.NEW);

        // Создание эпиков и подзадач
        Epic epic1 = new Epic(0, "Важный эпик 1", "Описание эпика 1");
        Subtask subtask1 = new Subtask(0, "Задача 1", "Описание задачи 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Задача 2", "Описание задачи 2", Status.NEW, epic1.getId());

        Epic epic2 = new Epic(0, "Важный эпик 2", "Описание эпика 2");
        Subtask subtask3 = new Subtask(0, "Задача 3", "Описание задачи 3", Status.NEW, epic2.getId());

        // Добавление задач и эпиков в менеджер
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);

        // Вызов метода printAllTasks
        printAllTasks(taskManager);

        // Обновление статусов и проверка
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        System.out.println("Обновленные задачи:");
        System.out.println(taskManager.getTask(task1.getId()));
        System.out.println(taskManager.getTask(task2.getId()));

        System.out.println("Обновленные эпики:");
        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println(taskManager.getEpic(epic2.getId()));

        System.out.println("Обновленные подзадачи:");
        System.out.println(taskManager.getSubtask(subtask1.getId()));
        System.out.println(taskManager.getSubtask(subtask2.getId()));
        System.out.println(taskManager.getSubtask(subtask3.getId()));

        // Удаление задач и эпиков
        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic1.getId());

        System.out.println("После удаления:");
        System.out.println("Все задачи:");
        System.out.println(taskManager.getTask(task1.getId())); // Должно быть null
        System.out.println(taskManager.getTask(task2.getId()));

        System.out.println("Все эпики:");
        System.out.println(taskManager.getEpic(epic1.getId())); // Должно быть null
        System.out.println(taskManager.getEpic(epic2.getId()));

        System.out.println("Все подзадачи:");
        System.out.println(taskManager.getSubtask(subtask1.getId())); // Должно быть null
        System.out.println(taskManager.getSubtask(subtask2.getId())); // Должно быть null
        System.out.println(taskManager.getSubtask(subtask3.getId()));
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
