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
import java.time.Duration;
import java.time.LocalDateTime;
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
    void testFileCreationAndDeletion() throws IOException {
        // Проверяем, что временный файл создан
        assertTrue(tempFile.exists());

        // После тестов удаляем временный файл
        Files.deleteIfExists(tempFile.toPath());

        // Проверяем, что файл удален
        assertFalse(tempFile.exists());
    }


    @Test
    void testSaveAndLoadEpics() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1");
        Epic epic2 = new Epic(2, "Epic 2", "Description 2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Epic> epics = loadedManager.getEpics();
        assertEquals(2, epics.size());
        assertEpicEquals(epic1, epics.get(0));
        assertEpicEquals(epic2, epics.get(1));
    }

    @Test
    void testSaveAndLoadSubtasks() {
        Epic epic = new Epic(1, "Epic 1", "Description 1");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2023, 1, 1, 9, 0), epic.getId());
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description 2", Status.IN_PROGRESS, Duration.ofMinutes(120), LocalDateTime.of(2023, 1, 1, 10, 0), epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Subtask> subtasks = loadedManager.getSubtasks();
        assertEquals(2, subtasks.size());
        assertSubtaskEquals(subtask1, subtasks.get(0));
        assertSubtaskEquals(subtask2, subtasks.get(1));
    }

    private void assertTaskEquals(Task expected, Task actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStartTime(), actual.getStartTime());
    }

    private void assertEpicEquals(Epic expected, Epic actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    private void assertSubtaskEquals(Subtask expected, Subtask actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getEpicId(), actual.getEpicId());
        assertEquals(expected.getDuration(), actual.getDuration());
        assertEquals(expected.getStartTime(), actual.getStartTime());
    }
}
