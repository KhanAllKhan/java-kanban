package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Task;

class Node {
    Task task;
    Node prev, next;

    Node(Task task) {
        this.task = task;
    }
}

