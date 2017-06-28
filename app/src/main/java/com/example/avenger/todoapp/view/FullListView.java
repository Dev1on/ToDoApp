package com.example.avenger.todoapp.view;

import com.example.avenger.todoapp.model.Todo;

public interface FullListView {

    void toggleDone();

    void toggleFavourite();

    void setToDos(Todo[] todos);

    void displayTodosNotFound();

    // TODO add sorting
}
