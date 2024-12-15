package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Task;
import kz.yandex.taskTracker.model.Epic;
import kz.yandex.taskTracker.model.Subtask;
import kz.yandex.taskTracker.model.Status;

import java.time.Duration;
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
            throw new IllegalArgumentException("Время задачи пересекается с уже существующей задачей.");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        historyManager.add(task);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Задача с ID " + id + " не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (isTimeConflict(task)) {
            throw new IllegalArgumentException("Время задачи пересекается с уже существующей задачей.");
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        historyManager.add(task);
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.remove(id);
        if (task == null) {
            throw new NotFoundException("Задача с ID " + id + " не найдена");
        }
        prioritizedTasks.remove(task);
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        List<Integer> taskIds = new ArrayList<>(tasks.keySet());
        for (Integer id : taskIds) {
            removeTask(id);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isTimeConflict(subtask)) {
            throw new IllegalArgumentException("Время подзадачи пересекается с уже существующей задачей.");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
            calculateFields(epic, getEpicSubtasks(epic.getId()));
        }
        prioritizedTasks.add(subtask);
        historyManager.add(subtask);
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с ID " + id + " не найдена");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isTimeConflict(subtask)) {
            throw new IllegalArgumentException("Время подзадачи пересекается с уже существующей задачей.");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
            calculateFields(epic, getEpicSubtasks(epic.getId()));
        }
        prioritizedTasks.add(subtask);
        historyManager.add(subtask);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача с ID " + id + " не найдена");
        }
        prioritizedTasks.remove(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(subtask.getId());
            updateEpicStatus(epic);
            calculateFields(epic, getEpicSubtasks(epic.getId()));
        }
        historyManager.remove(id);
    }

    @Override
    public void removeAllSubtasks() {
        List<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());
        for (Integer id : subtaskIds) {
            removeSubtask(id);
        }
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
            calculateFields(epic, new ArrayList<>());
        }
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        historyManager.add(epic);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с ID " + id + " не найден");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic existingEpic = epics.get(epic.getId());
        if (existingEpic == null) {
            throw new NotFoundException("Эпик с ID " + epic.getId() + " не найден");
        }
        existingEpic.setName(epic.getName());
        existingEpic.setStatus(epic.getStatus());
        existingEpic.setDuration(epic.getDuration());
        existingEpic.setStartTime(epic.getStartTime());
        calculateFields(existingEpic, getEpicSubtasks(existingEpic.getId()));
        historyManager.add(existingEpic);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            throw new NotFoundException("Эпик с ID " + id + " не найден");
        }
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        prioritizedTasks.remove(epic);
        historyManager.remove(id);
    }

    @Override
    public void removeAllEpics() {
        List<Integer> epicIds = new ArrayList<>(epics.keySet());
        for (Integer id : epicIds) {
            removeEpic(id);
        }
        subtasks.clear();
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
        return Optional.ofNullable(epics.get(epicId))
                .map(Epic::getSubtaskIds)
                .orElse(List.of())
                .stream()
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
            epic.setStatus(Status.NEW);
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
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // Метод для расчета полей эпика
    protected void calculateFields(Epic epic, List<Subtask> subtasks) {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (Subtask subtask : subtasks) {
            if (subtask != null) {
                Duration subtaskDuration = subtask.getDuration();
                LocalDateTime subtaskStartTime = subtask.getStartTime();

                if (subtaskStartTime != null && subtaskDuration != null) {
                    totalDuration = totalDuration.plus(subtaskDuration);

                    if (startTime == null || subtaskStartTime.isBefore(startTime)) {
                        startTime = subtaskStartTime;
                    }

                    LocalDateTime subtaskEndTime = subtaskStartTime.plus(subtaskDuration);

                    if (endTime == null || subtaskEndTime.isAfter(endTime)) {
                        endTime = subtaskEndTime;
                    }
                }
            }
        }

        epic.setStartTime(startTime);
        epic.setDuration(totalDuration);
        epic.setEndTime(endTime);

        System.out.println("Calculated total duration: " + totalDuration);
        System.out.println("Calculated startTime: " + startTime);
        System.out.println("Calculated endTime: " + endTime);
    }

    // Новый метод для получения idCounter
    protected int getIdCounter() {
        return idCounter;
    }

    // Новый метод для установки idCounter
    protected void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }
}

