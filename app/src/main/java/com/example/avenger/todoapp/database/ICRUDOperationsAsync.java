package com.example.avenger.todoapp.database;

import com.example.avenger.todoapp.model.Todo;

import java.util.List;

public interface ICRUDOperationsAsync {

    void createToDo(Todo item, CallbackFunction<Todo> callback);
    void readAllToDos(CallbackFunction<List<Todo>> callback);
    void readToDo(long id, CallbackFunction<Todo> callback);
    void updateToDo(long id, Todo item, CallbackFunction<Todo> callback);
    void deleteToDo(long id, CallbackFunction<Boolean> callback);

    // inner Interface for defining callback function
    interface CallbackFunction<T> {
        void process(T result);
    }
}
