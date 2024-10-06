package kz.yandex.taskTracker.service;

import kz.yandex.taskTracker.model.Task;

/*
    Не получилось откатить merge, как я не пытался
    Придумал написать коммит чтобы создать PR
     */
class Node {
    Task task;
    Node prev, next;

    Node(Task task) {
        this.task = task;
    }
}

