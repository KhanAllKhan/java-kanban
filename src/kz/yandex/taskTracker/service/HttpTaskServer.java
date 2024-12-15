package kz.yandex.taskTracker.service;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import kz.yandex.taskTracker.model.Task;
import kz.yandex.taskTracker.model.Subtask;
import kz.yandex.taskTracker.model.Epic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private static FileBackedTaskManager taskManager = Managers.getDefaultFileBackedManager();
    private HttpServer httpServer;

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/subtasks", new SubtaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());

        httpServer.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен");
    }

    static class TaskHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = "";

            try {
                if ("GET".equals(method)) {
                    List<Task> tasks = taskManager.getTasks();
                    response = gson.toJson(tasks);
                    sendText(exchange, response, 200);
                } else if ("POST".equals(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);
                    taskManager.addTask(task);
                    sendText(exchange, "Задача успешно добавлена", 201);
                } else if ("DELETE".equals(method)) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.substring(3));
                        taskManager.removeTask(id);
                        sendText(exchange, "Задача успешно удалена", 200);
                    } else {
                        sendNotFound(exchange);
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange);
            } catch (Exception e) {
                sendText(exchange, "Внутренняя ошибка сервера", 500);
            }
        }
    }

    static class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = "";

            try {
                if ("GET".equals(method)) {
                    List<Subtask> subtasks = taskManager.getSubtasks();
                    response = gson.toJson(subtasks);
                    sendText(exchange, response, 200);
                } else if ("POST".equals(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    taskManager.addSubtask(subtask);
                    sendText(exchange, "Подзадача успешно добавлена", 201);
                } else if ("DELETE".equals(method)) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.substring(3));
                        taskManager.removeSubtask(id);
                        sendText(exchange, "Подзадача успешно удалена", 200);
                    } else {
                        sendNotFound(exchange);
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange);
            } catch (Exception e) {
                sendText(exchange, "Внутренняя ошибка сервера", 500);
            }
        }
    }

    static class EpicHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = "";

            try {
                if ("GET".equals(method)) {
                    List<Epic> epics = taskManager.getEpics();
                    response = gson.toJson(epics);
                    sendText(exchange, response, 200);
                } else if ("POST".equals(method)) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);
                    taskManager.addEpic(epic);
                    sendText(exchange, "Эпик успешно добавлен", 201);
                } else if ("DELETE".equals(method)) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.substring(3));
                        taskManager.removeEpic(id);
                        sendText(exchange, "Эпик успешно удален", 200);
                    } else {
                        sendNotFound(exchange);
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange);
            } catch (Exception e) {
                sendText(exchange, "Внутренняя ошибка сервера", 500);
            }
        }
    }

    static class HistoryHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                List<Task> history = taskManager.getHistory();
                String response = gson.toJson(history);
                sendText(exchange, response, 200);
            } catch (Exception e) {
                sendText(exchange, "Внутренняя ошибка сервера", 500);
            }
        }
    }

    static class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = gson.toJson(prioritizedTasks);
                sendText(exchange, response, 200);
            } catch (Exception e) {
                sendText(exchange, "Внутренняя ошибка сервера", 500);
            }
        }
    }
}

class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.format(formatter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), formatter);
    }
}

class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(duration.toString());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        return Duration.parse(jsonReader.nextString());
    }
}