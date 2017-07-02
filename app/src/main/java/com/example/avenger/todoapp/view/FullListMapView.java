package com.example.avenger.todoapp.view;

import android.view.View;

import com.example.avenger.todoapp.model.Todo;

import java.util.ArrayList;

public interface FullListMapView {

    void updateView(ArrayList<Todo> todos);

    void displayTodosNotFound();

    void startDetail(long id);

    void setTodos(ArrayList<Todo> todos);

}
