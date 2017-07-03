package com.example.avenger.todoapp.database;

import android.app.Application;
import android.util.Log;

import com.example.avenger.todoapp.model.Todo;

import java.util.ArrayList;
import java.util.List;

import static com.example.avenger.todoapp.model.AppSettingsConstants.CREATE_TEST_DATA_LOCAL_DB;

public class DBApplication extends Application {

    private static final String logger = DBApplication.class.getSimpleName();

    private ICRUDOperationsAsync crudOperations;

    public ICRUDOperationsAsync getCrudOperations() {
        return crudOperations;
    }

    public void setWebApplicationAvailable(boolean available) {
        crudOperations = new DBCRUDOperations(getApplicationContext(), available);

        if (CREATE_TEST_DATA_LOCAL_DB) {
            createLocalTestData(crudOperations);
        }

        if (available) {
            final List<Todo> localTodos = new ArrayList<>();
            crudOperations.readAllToDos(result -> {
                localTodos.addAll(result);
                if (localTodos.size() > 0) {
                    ((DBCRUDOperations)crudOperations).deleteAllTodosOnline();
                    for (Todo todo : localTodos) {
                        ((DBCRUDOperations)crudOperations).createItemOnline(todo);
                    }
                } else {
                    List<Todo> remoteTodos = ((DBCRUDOperations)crudOperations).readAllTodosOnline();
                }
            });
        }
    }

    private void createLocalTestData(ICRUDOperationsAsync crudOperations) {
        Todo newItem1 = new Todo("Name1", "Description1");
        newItem1.setDone(true);
        newItem1.setExpiry(123);
        newItem1.setFavourite(false);
        newItem1.setLocation(new Todo.Location("Havanna", new Todo.LatLng(2,3)));
        ArrayList<String> con1 = new ArrayList<>();
        con1.add("Contact 1");
        con1.add("Contact 2");
        newItem1.setContacts(con1);

        Todo newItem2 = new Todo("Name2", "Description2");
        newItem2.setDone(true);
        newItem2.setExpiry(1234);
        newItem2.setFavourite(true);
        newItem2.setLocation(new Todo.Location("Manhattan", new Todo.LatLng(2,3)));
        ArrayList<String> con2 = new ArrayList<>();
        con2.add("Contact 2");
        con2.add("Contact 3");
        newItem2.setContacts(con2);

        Todo newItem3 = new Todo("Name3", "Description3");
        newItem3.setDone(false);
        newItem3.setExpiry(1235);
        newItem3.setFavourite(true);
        newItem3.setLocation(new Todo.Location("Warschau", new Todo.LatLng(2,3)));
        ArrayList<String> con3 = new ArrayList<>();
        con3.add("Contact 5");
        con3.add("Contact 8");
        newItem3.setContacts(con3);

        crudOperations.createToDo(newItem1, (Todo result) -> Log.d(logger, "Item 1 created with id: " + result.getId()));
        crudOperations.createToDo(newItem2, (Todo result) -> Log.d(logger, "Item 2 created with id: " + result.getId()));
        crudOperations.createToDo(newItem3, (Todo result) -> Log.d(logger, "Item 3 created with id: " + result.getId()));
    }
}
