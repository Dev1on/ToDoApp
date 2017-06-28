package com.example.avenger.todoapp.presenter;

import android.util.Log;

import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.database.ICRUDOperationsAsync;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.view.FullListView;

import java.util.ArrayList;
import java.util.List;


public class FullListPresenter {

    private FullListView fullListView;
    private ICRUDOperationsAsync crudOperations;
    private ArrayList<Todo> todos = new ArrayList<>();

    public FullListPresenter(FullListView fullListView, DBApplication application) {
        this.fullListView = fullListView;
        crudOperations = application.getCrudOperations();
    }

    public void readAllToDos() {
        crudOperations.readAllToDos(result -> {

            if(result.size() == 0) {
                fullListView.displayTodosNotFound();
            } else {
                ArrayList<Todo> todos = new ArrayList<Todo>();
                todos.addAll(result);
                setTodos(todos);
                Log.i("afterReadAllTodoBefInit", ".");
                fullListView.initializeView(todos);
            }
        });
    }

    public void setTodos(List<Todo> todos) {
        this.todos.addAll(todos);
    }

    public ArrayList<Todo> getTodos() {
        return todos;
    }

    public void onDestroy() {
        fullListView = null;
    }
}
