package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private final Node head;
    private final Node tail;

    public InMemoryHistoryManager() {
        head = new Node(null);
        tail = new Node(null);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        Node newNode = new Node(task);
        linkLast(newNode);
        history.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Node node) {
        Node prevNode = tail.prev;
        prevNode.next = node;
        node.prev = prevNode;
        node.next = tail;
        tail.prev = node;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head.next;
        while (current != tail) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }
}
