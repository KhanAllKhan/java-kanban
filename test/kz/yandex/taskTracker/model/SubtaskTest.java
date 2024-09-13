package kz.yandex.taskTracker.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    @Test
    public void testSubtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", Status.NEW, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            subtask.setEpicId(1);
        });
    }
}