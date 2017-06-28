package com.example.avenger.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.avenger.todoapp.model.Todo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.avenger.todoapp.R.color.todo;

public class DBCRUDOperations implements ICRUDOperationsAsync {

    protected static String logger = DBCRUDOperations.class.getSimpleName();

    private static final String DB_NAME = "TODOS";

    private boolean webApplicationAvailable;
    private RemoteDBCRUDOperations remoteDBCRUDOperations;

    private SQLiteDatabase db;

    public DBCRUDOperations(Context context, boolean webApplicationAvailable) {
        this.webApplicationAvailable = webApplicationAvailable;

        db = context.openOrCreateDatabase("mydb.sqlite", Context.MODE_PRIVATE, null);
        if (db.getVersion() == 0) {
            db.setVersion(1);
            db.execSQL("CREATE TABLE " + DB_NAME + " (ID INTEGER PRIMARY KEY, NAME TEXT, DESCRIPTION TEXT, EXPIRY INTEGER, DONE INTEGER, FAVOURITE INTEGER, LAENGENGRAD INTEGER, BREITENGRAD INTEGER, LOCATIONNAME TEXT)");
            db.execSQL("CREATE TABLE CONTACTS (ID INTEGER PRIMARY KEY, NAME TEXT, NUMBER TEXT)");
            db.execSQL("CREATE TABLE TODOSCONTACTS (TODOID INTEGER REFERENCES TODOS(ID), CONTACTID INTEGER REFERENCES CONTACTS(ID), PRIMARY KEY(TODOID, CONTACTID))");
        }
        // (un)comment to keep todos in database
        db.execSQL("DELETE FROM " + DB_NAME);

        if(webApplicationAvailable) {
            remoteDBCRUDOperations = new RemoteDBCRUDOperations(context);
        }
    }

    @Override
    public void createToDo(Todo todo, CallbackFunction<Todo> callback) {
        new AsyncTask<Todo, Void, Todo>() {
            @Override
            protected Todo doInBackground(Todo... params) {
                ContentValues values = setContentValuesForTodo(todo);
                long id = db.insertOrThrow(DB_NAME, null, values);
                todo.setId(id);

                if(webApplicationAvailable) {
                    try {
                        remoteDBCRUDOperations.getWebAPI().createToDoItem(todo).execute().body();
                    } catch (IOException e) {
                        Log.d(logger, "Item not created in WebApplication.");
                        e.printStackTrace();
                    }
                }
                return todo;
            }

            @Override
            protected void onPostExecute(Todo todo) {
                callback.process(todo);
            }
        }.execute(todo);
    }

    @Override
    public void readAllToDos(CallbackFunction<List<Todo>> callback) {
        new AsyncTask<Void, Void, List<Todo>>() {
            @Override
            protected List<Todo> doInBackground(Void... params) {
                List<Todo> todos = new ArrayList<>();

                Cursor cursor = db.query(DB_NAME, new String[]{"ID", "NAME", "DESCRIPTION", "EXPIRY", "DONE", "FAVOURITE", "LAENGENGRAD", "BREITENGRAD", "LOCATIONNAME"}, null, null, null, null, "ID");
                if(cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    boolean next;
                    do {
                        Todo todo = setTodoFromDB(cursor);
                        todos.add(todo);
                        next = cursor.moveToNext();
                    } while (next);
                }
                cursor.close();
                return todos;
            }

            @Override
            protected void onPostExecute(List<Todo> todos) {
                callback.process(todos);
            }
        }.execute();
    }

    @Override
    public void readToDo(long id, CallbackFunction<Todo> callback) {
        new AsyncTask<Long, Void, Todo>() {
            @Override
            protected Todo doInBackground(Long... params) {
                Cursor cursor = db.query(DB_NAME, new String[]{"ID", "NAME", "DESCRIPTION", "EXPIRY", "DONE", "FAVOURITE", "LAENGENGRAD", "BREITENGRAD", "LOCATIONNAME"}, null, null, null, null, "ID");
                Todo todo = null;
                if(cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    boolean next;
                    do {
                        if (cursor.getLong(cursor.getColumnIndex("ID")) == id) {
                            todo = setTodoFromDB(cursor);
                            break;
                        }
                        next = cursor.moveToNext();
                    } while (next);
                }
                cursor.close();
                return todo;
            }

            @Override
            protected void onPostExecute(Todo todo) {
                callback.process(todo);
            }
        }.execute(id);
    }

    @Override
    public void updateToDo(long id, Todo todo, CallbackFunction<Todo> callback) {
        new AsyncTask<Long, Void, Todo>() {
            @Override
            protected Todo doInBackground(Long... params) {
                ContentValues values = setContentValuesForTodo(todo);
                int rowsAffected = db.update(DB_NAME, values, "ID=?", new String[]{String.valueOf(id)});
                if (rowsAffected > 0) {
                    if(webApplicationAvailable) {
                        try {
                            remoteDBCRUDOperations.getWebAPI().updateToDoItem(todo.getId(), todo).execute().body();
                        } catch (IOException e) {
                            Log.d(logger, "Could not update item with id: " + id);
                            e.printStackTrace();
                        }
                    }
                    return todo;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Todo todo) {
                callback.process(todo);
            }
        }.execute(id);
    }

    @Override
    public void deleteToDo(long id, CallbackFunction<Boolean> callback) {
        new AsyncTask<Long, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Long... params) {
                int rowsAffected = db.delete(DB_NAME, "ID=?", new String[]{String.valueOf(id)});
                if(webApplicationAvailable) {
                    try {
                        remoteDBCRUDOperations.getWebAPI().deleteToDoItem(id).execute().body();
                    } catch (IOException e) {
                        Log.d(logger, "Could not delete item with id: " + id);
                        e.printStackTrace();
                    }
                }
                return rowsAffected > 0;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                callback.process(aBoolean);
            }
        }.execute(id);
    }

    @NonNull
    private Todo setTodoFromDB(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex("ID"));
        String name = cursor.getString(cursor.getColumnIndex("NAME"));
        String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
        long expiry = cursor.getLong(cursor.getColumnIndex("EXPIRY"));
        boolean done = (cursor.getInt(cursor.getColumnIndex("DONE"))) == 1;
        boolean favourite = (cursor.getInt(cursor.getColumnIndex("FAVOURITE"))) == 1;
        long lat = cursor.getLong(cursor.getColumnIndex("LAENGENGRAD"));
        long lng = cursor.getLong(cursor.getColumnIndex("BREITENGRAD"));
        String locationName = cursor.getString(cursor.getColumnIndex("LOCATIONNAME"));
        Todo.Location location = new Todo.Location(locationName, new Todo.LatLng(lat,lng));
        // TODO read out all contacts
        List<String> contacts = new ArrayList<>();

        Todo todo = new Todo(name, description);
        todo.setId(id);
        todo.setExpiry(expiry);
        todo.setDone(done);
        todo.setFavourite(favourite);
        todo.setLocation(location);
        todo.setContacts(contacts);

        return todo;
    }

    @NonNull
    private ContentValues setContentValuesForTodo(Todo todo) {
        ContentValues values = new ContentValues();
        values.put("NAME", todo.getName());
        values.put("DESCRIPTION", todo.getDescription());
        values.put("EXPIRY", todo.getExpiry());
        values.put("DONE", todo.isDone());
        values.put("FAVOURITE", todo.isFavourite());
        values.put("LAENGENGRAD", todo.getLocation().getLatlng().getLat());
        values.put("BREITENGRAD", todo.getLocation().getLatlng().getLng());
        values.put("LOCATIONNAME", todo.getLocation().getName());

        // TODO add contacts to db

        return values;
    }
}