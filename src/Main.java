import kz.yandex.taskTracker.model.*;
import kz.yandex.taskTracker.service.FileBackedTaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager taskManager;

        if (file.exists()) {
            taskManager = FileBackedTaskManager.loadFromFile(file);
        } else {
            taskManager = new FileBackedTaskManager(file);
        }

        Epic epic = new Epic(1, "Эпик задача", "Описание эпика");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Описание подзадачи 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2022, 1, 1, 12, 0), epic.getId());
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Описание подзадачи 2", Status.IN_PROGRESS, Duration.ofMinutes(45), LocalDateTime.of(2022, 1, 2, 14, 0), epic.getId());
        taskManager.addSubtask(subtask2);

        Task task = new Task(4, "Обычная задача", "Описание обычной задачи", Status.DONE, Duration.ofMinutes(60), LocalDateTime.of(2022, 1, 3, 10, 0));
        taskManager.addTask(task);

        taskManager.updateTask(new Task(task.getId(), task.getName(), "Обновлённое описание обычной задачи", task.getStatus(), task.getDuration(), task.getStartTime()));

        System.out.println("Загруженные задачи:");
        for (Task loadedTask : taskManager.getTasks()) {
            System.out.println(loadedTask);
        }
        for (Epic loadedEpic : taskManager.getEpics()) {
            System.out.println(loadedEpic);
        }
        for (Subtask loadedSubtask : taskManager.getSubtasks()) {
            System.out.println(loadedSubtask);
        }

        System.out.println("\nЗадачи в порядке приоритета:");
        for (Task prioritizedTask : taskManager.getPrioritizedTasks()) {
            System.out.println(prioritizedTask);
        }
    }
}
