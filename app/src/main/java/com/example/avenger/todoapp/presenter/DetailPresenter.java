package com.example.avenger.todoapp.presenter;

import android.util.Log;

import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.database.ICRUDOperationsAsync;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.view.DetailView;

public class DetailPresenter {

    private DetailView detailView;
    private ICRUDOperationsAsync crudOperations;
    private Todo todo;

    public DetailPresenter(DetailView detailView, DBApplication application) {
        this.detailView = detailView;
        crudOperations = application.getCrudOperations();
    }

    public void createItem() {
        Todo newTodo = detailView.getCurrentTodo();
        crudOperations.createToDo(newTodo, result -> {
            setTodo(result);
            detailView.setTodo(result);
        });
    }

    public void saveItem() {
        Todo newTodo = detailView.getCurrentTodo();



        crudOperations.updateToDo(todo.getId(), newTodo, result -> {
            setTodo(result);
            detailView.setTodo(result);
        });
    }

    public void readToDo(long id) {
        crudOperations.readToDo(id, result -> {
            Log.d("DetailPresenter", "Result is: " + result);

            if(null == result || result.getId() == 0) {
                detailView.displayTodoNotFound();
            } else {
                setTodo(result);
                detailView.setTodo(result);
            }
        });
    }

    public void deleteToDo(long id) {
        crudOperations.deleteToDo(id, result -> {
            Log.d("DetailPresenter", "Removed todo with ID: " + id);
        });
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public void onDestroy() {
        detailView = null;
    }
}
