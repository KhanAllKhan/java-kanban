package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    private int idCounter = 0;

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTimeConflict(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }

        LocalDateTime newTaskStart = newTask.getStartTime();
        LocalDateTime newTaskEnd = newTask.getEndTime();

        return prioritizedTasks.stream().anyMatch(existingTask -> {
            if (existingTask.getStartTime() == null || existingTask.getDuration() == null) {
                return false;
            }

            LocalDateTime existingTaskStart = existingTask.getStartTime();
            LocalDateTime existingTaskEnd = existingTask.getEndTime();

            return newTaskStart.isBefore(existingTaskEnd) && newTaskEnd.isAfter(existingTaskStart);
        });
    }

    @Override
    public void addTask(Task task) {
        if (isTimeConflict(task)) {
            throw new IllegalArgumentException("Task time conflicts with an existing task.");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        historyManager.add(task); // Добавляем задачу в историю
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (isTimeConflict(task)) {
            throw new IllegalArgumentException("Task time conflicts with an existing task.");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        historyManager.add(task); // Обновляем историю
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id); // Удаляем из истории
        }
    }

    @Override
    public void removeAllTasks() {
        List<Integer> taskIds = new ArrayList<>(tasks.keySet());
        for (Integer id : taskIds) {
            removeTask(id); // Удаляем каждую задачу и из historyManager
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isTimeConflict(subtask)) {
            throw new IllegalArgumentException("Subtask time conflicts with an existing task.");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
        }
        prioritizedTasks.add(subtask);
        historyManager.add(subtask); // Добавляем подзадачу в историю
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isTimeConflict(subtask)) {
            throw new IllegalArgumentException("Subtask time conflicts with an existing task.");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
        prioritizedTasks.add(subtask);
        historyManager.add(subtask); // Обновляем историю
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
                updateEpicStatus(epic);
            }
            historyManager.remove(id); // Удаляем из истории
        }
    }

    @Override
    public void removeAllSubtasks() {
        List<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());
        for (Integer id : subtaskIds) {
            removeSubtask(id); // Удаляем каждую подзадачу и из historyManager
        }
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        historyManager.add(epic); // Добавляем эпик в историю
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic existingEpic = epics.get(epic.getId());
        if (existingEpic != null) {
            existingEpic.setName(epic.getName());
            existingEpic.setStatus(epic.getStatus());
            existingEpic.setDuration(epic.getDuration());
            existingEpic.setStartTime(epic.getStartTime());
            existingEpic.calculateFields(getEpicSubtasks(epic.getId()));
            historyManager.add(existingEpic); // Обновляем историю
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId); // Удаляем подзадачу из истории
            }
            prioritizedTasks.remove(epic);
            historyManager.remove(id); // Удаляем эпик из истории
        }
    }

    @Override
    public void removeAllEpics() {
        List<Integer> epicIds = new ArrayList<>(epics.keySet());
        for (Integer id : epicIds) {
            removeEpic(id); // Удаляем каждый эпик и из historyManager
        }
        subtasks.clear(); // Убираем все подзадачи, так как они тоже связаны с эпиками
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Приватные методы
    protected int generateId() {
        return ++idCounter;
    }

    protected void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW); // Убедитесь, что этот метод есть в Task
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW); // Убедитесь, что этот метод есть в Task
        } else if (allDone) {
            epic.setStatus(Status.DONE); // Убедитесь, что этот метод есть в Task
        } else {
            epic.setStatus(Status.IN_PROGRESS); // Убедитесь, что этот метод есть в Task
        }
    }

    // Новые методы для доступа к idCounter
    protected int getIdCounter() {
        return idCounter;
    }

    protected void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }
}

