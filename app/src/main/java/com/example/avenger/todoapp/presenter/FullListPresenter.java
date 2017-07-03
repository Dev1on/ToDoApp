package com.example.avenger.todoapp.presenter;

import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.database.ICRUDOperationsAsync;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.view.FullListView;

import java.util.ArrayList;
import java.util.List;


public class FullListPresenter {

    private FullListView fullListView;
    private static ICRUDOperationsAsync crudOperations;
    private final ArrayList<Todo> todos = new ArrayList<>();

    public FullListPresenter(FullListView fullListView, DBApplication application) {
        this.fullListView = fullListView;
        crudOperations = application.getCrudOperations();
    }

    public void readAllToDosForInit() {
        crudOperations.readAllToDos(result -> {
            ArrayList<Todo> todos = new ArrayList<>();
            todos.addAll(result);
            setTodos(todos);
            fullListView.initializeView(todos);
            if(result.size() == 0) {
                fullListView.displayTodosNotFound();
            }
        });
    }

    public void readAllToDosForChanges() {
        crudOperations.readAllToDos(result -> {
            ArrayList<Todo> todos = new ArrayList<>();
            todos.addAll(result);
            setTodos(todos);
            fullListView.updateView(todos);
            fullListView.updateMapView(todos);
            if(result.size() == 0) {
                fullListView.displayTodosNotFound();
            }
        });
    }

    public void updateTodo(Todo todo) {
        crudOperations.updateToDo(todo.getId(), todo, result -> readAllToDosForChanges());
    }

    public void updateTodoWithIDInList(long id) {
        crudOperations.readToDo(id, result -> {
            for(Todo todoInList : getTodos()) {
                if(todoInList.getId() == result.getId()) {
                    int index = getTodos().indexOf(todoInList);
                    getTodos().set(index, result);
                    fullListView.updateView(todos);
                    fullListView.updateMapView(todos);
                    return;
                }
            }
        });
    }

    public void removeTodoWithIDFromList(long id) {
        for(Todo todoInList : getTodos()) {
            if(todoInList.getId() == id) {
                int index = getTodos().indexOf(todoInList);
                getTodos().remove(index);
                fullListView.updateView(todos);
                fullListView.updateMapView(todos);
                return;
            }
        }
    }

    public void setTodos(List<Todo> todos) {
        this.todos.clear();
        this.todos.addAll(todos);
    }

    public ArrayList<Todo> getTodos() {
        return todos;
    }

    public void onDestroy() {
        fullListView = null;
    }
}
