public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создание задач
        Task task1 = new Task(manager.generateId(), "Переезд", "Собрать вещи", Status.NEW);
        Task task2 = new Task(manager.generateId(), "Собрать коробки", "Упаковать вещи", Status.NEW);

        // Создание эпиков и подзадач
        Epic epic1 = new Epic(manager.generateId(), "Важный эпик 1", "Описание эпика 1", Status.NEW);
        Subtask subtask1 = new Subtask(manager.generateId(), "Задача 1", "Описание задачи 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(manager.generateId(), "Задача 2", "Описание задачи 2", Status.NEW, epic1.getId());

        Epic epic2 = new Epic(manager.generateId(), "Важный эпик 2", "Описание эпика 2", Status.NEW);
        Subtask subtask3 = new Subtask(manager.generateId(), "Задача 3", "Описание задачи 3", Status.NEW, epic2.getId());

        // Добавление задач и эпиков в менеджер
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);

        // Отображение всех задач, эпиков и подзадач
        System.out.println("Все задачи:");
        System.out.println(manager.getTask(task1.getId()));
        System.out.println(manager.getTask(task2.getId()));

        System.out.println("Все эпики:");
        System.out.println(manager.getEpic(epic1.getId()));
        System.out.println(manager.getEpic(epic2.getId()));

        System.out.println("Все подзадачи:");
        System.out.println(manager.getSubtask(subtask1.getId()));
        System.out.println(manager.getSubtask(subtask2.getId()));
        System.out.println(manager.getSubtask(subtask3.getId()));

        // Обновление статусов и проверка
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateEpicStatus(epic1);

        System.out.println("Обновленные задачи:");
        System.out.println(manager.getTask(task1.getId()));
        System.out.println(manager.getTask(task2.getId()));

        System.out.println("Обновленные эпики:");
        System.out.println(manager.getEpic(epic1.getId()));
        System.out.println(manager.getEpic(epic2.getId()));

        System.out.println("Обновленные подзадачи:");
        System.out.println(manager.getSubtask(subtask1.getId()));
        System.out.println(manager.getSubtask(subtask2.getId()));
        System.out.println(manager.getSubtask(subtask3.getId()));

        // Удаление задач и эпиков
        manager.removeTask(task1.getId());
        manager.removeEpic(epic1.getId());

        System.out.println("После удаления:");
        System.out.println("Все задачи:");
        System.out.println(manager.getTask(task1.getId()));
        System.out.println(manager.getTask(task2.getId()));

        System.out.println("Все эпики:");
        System.out.println(manager.getEpic(epic1.getId()));
        System.out.println(manager.getEpic(epic2.getId()));

        System.out.println("Все подзадачи:");
        System.out.println(manager.getSubtask(subtask1.getId()));
        System.out.println(manager.getSubtask(subtask2.getId()));
        System.out.println(manager.getSubtask(subtask3.getId()));
    }
}
