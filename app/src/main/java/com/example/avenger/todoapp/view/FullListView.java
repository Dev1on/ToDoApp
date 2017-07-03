package com.example.avenger.todoapp.view;

import android.view.View;
import com.example.avenger.todoapp.model.Todo;
import java.util.ArrayList;

public interface FullListView {

    void toggleDone(Todo todo);

    void toggleFavourite(Todo todo);

    void initializeView(ArrayList<Todo> todos);

    void updateView(ArrayList<Todo> todos);

    void updateMapView(ArrayList<Todo> todos);

    void displayTodosNotFound();

    void startDetail(View v, long id);

}
