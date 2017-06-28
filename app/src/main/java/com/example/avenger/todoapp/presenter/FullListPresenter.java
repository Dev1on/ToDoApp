package com.example.avenger.todoapp.presenter;

import android.util.Log;

import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.database.ICRUDOperationsAsync;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.view.FullListView;


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
                Todo[] todos = new Todo[result.size()];
                result.toArray(todos); // fill the array
                setTodos(todos);
                fullListView.initializeView(todos);
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
