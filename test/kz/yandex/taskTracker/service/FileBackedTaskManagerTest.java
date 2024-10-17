package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Epic;
import kz.yandex.taskTracker.model.Status;
import kz.yandex.taskTracker.model.Subtask;
import kz.yandex.taskTracker.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем временный файл перед каждым тестом
        tempFile = File.createTempFile("test", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        // Проверяем, что изначально файл пуст
        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());

        // Сохраняем пустой менеджер
        manager.save();

        // Загружаем данные из того же файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что загруженные данные также пусты
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    @Test
    void testSaveAndLoadMultipleTasks() {
        // Создаем и добавляем несколько задач
        Task task1 = new Task(1, "Переезд", "Собрать вещи", Status.NEW);
        Task task2 = new Task(2, "Собрать коробки", "Упаковать вещи", Status.NEW);
        Epic epic1 = new Epic(3, "Эпик", "Описание эпика");
        Subtask subtask1 = new Subtask(4, "Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        // Сохраняем менеджер
        manager.save();

        // Загружаем данные из того же файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что загруженные данные совпадают с сохраненными
        List<Task> loadedTasks = loadedManager.getTasks();
        List<Epic> loadedEpics = loadedManager.getEpics();
        List<Subtask> loadedSubtasks = loadedManager.getSubtasks();

        assertEquals(2, loadedTasks.size());
        assertEquals(1, loadedEpics.size());
        assertEquals(1, loadedSubtasks.size());

        assertEquals(task1, loadedTasks.get(0));
        assertEquals(task2, loadedTasks.get(1));
        assertEquals(epic1, loadedEpics.get(0));
        assertEquals(subtask1, loadedSubtasks.get(0));
    }

    @Test
    void testSaveAndLoadWithHistory() {
        // Создаем задачи и добавляем их в историю просмотра
        Task task1 = new Task(5, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(6, "Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        Epic epic1 = new Epic(7, "Эпик", "Описание эпика");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);

        // Просматриваем задачи (для истории)
        manager.getTask(task1.getId());
        manager.getTask(task2.getId());
        manager.getEpic(epic1.getId());

        // Сохраняем менеджер
        manager.save();

        // Загружаем данные из того же файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем историю
        List<Task> history = loadedManager.getHistory();
        assertEquals(3, history.size()); // Исправлено на 3, так как эпик тоже в истории
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(epic1, history.get(2));
    }




    @Test
    void testFileCreationAndDeletion() throws IOException {
        // Проверяем, что временный файл создан
        assertTrue(tempFile.exists());

        // После тестов удаляем временный файл
        Files.deleteIfExists(tempFile.toPath());

        // Проверяем, что файл удален
        assertFalse(tempFile.exists());
    }
}
