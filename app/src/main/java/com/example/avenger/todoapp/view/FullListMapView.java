package com.example.avenger.todoapp.view;

import android.view.View;

import com.example.avenger.todoapp.model.Todo;

import java.util.ArrayList;

public interface FullListMapView {

    void displayTodosNotFound();

    void startDetail(long id);

    void fillWithTodos(ArrayList<Todo> todos);

}
