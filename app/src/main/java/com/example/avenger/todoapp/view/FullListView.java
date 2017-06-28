package com.example.avenger.todoapp.view;

import com.example.avenger.todoapp.model.Todo;

import java.util.List;

public interface FullListView {

    void toggleDone();

    void toggleFavourite();

    void initializeView(List<Todo> todos);

    void displayTodosNotFound();

    // TODO add sorting
}
