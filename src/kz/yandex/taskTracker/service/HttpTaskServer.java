package kz.yandex.taskTracker.service;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import kz.yandex.taskTracker.Handlers.TaskHandler;
import kz.yandex.taskTracker.Handlers.SubtaskHandler;
import kz.yandex.taskTracker.Handlers.EpicHandler;
import kz.yandex.taskTracker.Handlers.HistoryHandler;
import kz.yandex.taskTracker.Handlers.PrioritizedHandler;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private static FileBackedTaskManager taskManager = Managers.getDefaultFileBackedManager();
    private HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен");
    }
}