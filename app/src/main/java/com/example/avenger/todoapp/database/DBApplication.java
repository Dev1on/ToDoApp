package com.example.avenger.todoapp.database;

import android.app.Application;
import android.util.Log;

import com.example.avenger.todoapp.model.Todo;

import java.util.ArrayList;
import java.util.List;

public class DBApplication extends Application {

    private static String logger = DBApplication.class.getSimpleName();

    private boolean webApplicationAvailable;

    private ICRUDOperationsAsync crudOperations;

    @Override
    public void onCreate() {
        super.onCreate();
        crudOperations = new DBCRUDOperations(getApplicationContext(), webApplicationAvailable);

        // /(un)comment for local test data
        createLocalTestData(crudOperations);

        final List<Todo>[] localTodos = new List[]{new ArrayList<>()};
        crudOperations.readAllToDos(result -> {
            Log.i("DBAP", "Result is: " + result.size());
            localTodos[0].addAll(result);
            Log.i("DBApplication", "localData exists: " + localTodos[0].size());
            if (localTodos[0].size() > 0) {
                //TODO if local db has data, then delete all online data and push all local data to remote db

            }
        });
    }

    public ICRUDOperationsAsync getCrudOperations() {
        return crudOperations;
    }

    public void setWebApplicationAvailable(boolean available) {
        webApplicationAvailable= available;
    }

    private void createLocalTestData(ICRUDOperationsAsync crudOperations) {
        // TODO extend local test data with contacts

        Todo newItem1 = new Todo("Name1", "Description1");
        newItem1.setDone(true);
        newItem1.setExpiry(123);
        newItem1.setFavourite(false);
        newItem1.setLocation(new Todo.Location("Havanna", new Todo.LatLng(2,3)));

        Todo newItem2 = new Todo("Name2", "Description2");
        newItem2.setDone(true);
        newItem2.setExpiry(1234);
        newItem2.setFavourite(true);
        newItem2.setLocation(new Todo.Location("Manhattan", new Todo.LatLng(2,3)));

        Todo newItem3 = new Todo("Name3", "Description3");
        newItem3.setDone(false);
        newItem3.setExpiry(1235);
        newItem3.setFavourite(true);
        newItem3.setLocation(new Todo.Location("Warschau", new Todo.LatLng(2,3)));

        crudOperations.createToDo(newItem1, (Todo result) -> Log.d(logger, "Item 1 created with id: " + result.getId()));
        crudOperations.createToDo(newItem2, (Todo result) -> Log.d(logger, "Item 2 created with id: " + result.getId()));
        crudOperations.createToDo(newItem3, (Todo result) -> Log.d(logger, "Item 3 created with id: " + result.getId()));
    }
}
