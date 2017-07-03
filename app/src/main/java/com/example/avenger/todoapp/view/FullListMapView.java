package com.example.avenger.todoapp.view;

import com.example.avenger.todoapp.model.Todo;

import java.util.ArrayList;

public interface FullListMapView {

    void displayTodosNotFound();

    void startDetail(long id);

    void fillWithTodos(ArrayList<Todo> todos);

    void updateListView(ArrayList<Todo> todos);

}
