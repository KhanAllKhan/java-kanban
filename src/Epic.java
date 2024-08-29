import java.util.ArrayList;
import java.util.List;
public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtasks = new ArrayList<>();
    }
}
