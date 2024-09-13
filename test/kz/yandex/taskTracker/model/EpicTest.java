package kz.yandex.taskTracker.model;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;

public class EpicTest {
    @Test
    public void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic(1, "Epic 1", "Description 1");

        assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(1);
        });
    }
}