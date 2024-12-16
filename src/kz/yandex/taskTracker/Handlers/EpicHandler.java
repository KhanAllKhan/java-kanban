package kz.yandex.taskTracker.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kz.yandex.taskTracker.model.Epic;
import kz.yandex.taskTracker.service.BaseHttpHandler;
import kz.yandex.taskTracker.service.FileBackedTaskManager;
import kz.yandex.taskTracker.service.NotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final FileBackedTaskManager taskManager;
    private final Gson gson;

    public EpicHandler(FileBackedTaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

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