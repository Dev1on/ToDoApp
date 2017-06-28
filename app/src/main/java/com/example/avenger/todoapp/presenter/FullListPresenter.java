package com.example.avenger.todoapp.presenter;

import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.database.ICRUDOperationsAsync;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.view.FullListView;

import java.util.ArrayList;
import java.util.List;

public class FullListPresenter {

    private FullListView fullListView;
    private ICRUDOperationsAsync crudOperations;
    private Todo[] todos;

    public FullListPresenter(FullListView fullListView, DBApplication application) {
        this.fullListView = fullListView;
        crudOperations = application.getCrudOperations();
    }

    public void readAllToDos() {
        crudOperations.readAllToDos(result -> {

            if(result.size() == 0) {
                fullListView.displayTodosNotFound();
            } else {
                setTodos(result);
                fullListView.setToDos(result);
            }
        });
    }

    public void setTodos(Todo[] todos) {
        this.todos = todos;
    }

    public void onDestroy() {
        fullListView = null;
    }
}
