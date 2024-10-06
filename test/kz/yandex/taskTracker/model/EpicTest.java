package kz.yandex.taskTracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    @Test
    public void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description 1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(1);
        });

        assertEquals("Epic cannot add itself as a subtask", exception.getMessage());
    }
}
