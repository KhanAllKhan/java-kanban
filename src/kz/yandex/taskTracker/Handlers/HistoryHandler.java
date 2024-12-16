package kz.yandex.taskTracker.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kz.yandex.taskTracker.service.BaseHttpHandler;
import kz.yandex.taskTracker.service.FileBackedTaskManager;

import java.io.IOException;
import java.util.List;

import kz.yandex.taskTracker.model.Task;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final FileBackedTaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(FileBackedTaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

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