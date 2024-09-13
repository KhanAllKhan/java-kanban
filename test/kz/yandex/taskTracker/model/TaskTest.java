package kz.yandex.taskTracker.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void testTasksEquality() {
        Task task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(1, "Task 2", "Description 2", Status.NEW);

        assertEquals(task1, task2);
    }
    @Test
    public void testEpicsEquality() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1");
        Epic epic2 = new Epic(1, "Epic 2", "Description 2");

        assertEquals(epic1, epic2);
    }
}
