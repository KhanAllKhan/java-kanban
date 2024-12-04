package kz.yandex.taskTracker.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now(), 2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subtask.setEpicId(1);
        });

        assertEquals("Подзадача не может быть своим собственным эпиком", exception.getMessage());
    }
}
