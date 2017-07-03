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
import java.util.Collections;
import java.util.List;

import static com.example.avenger.todoapp.model.AppSettingsConstants.DELETE_LOCAL_DB;

public class DBCRUDOperations implements ICRUDOperationsAsync {

    private static final String logger = DBCRUDOperations.class.getSimpleName();

    private static final String DB_NAME = "TODOS";

    private final boolean webApplicationAvailable;
    private RemoteDBCRUDOperations remoteDBCRUDOperations;

    private final SQLiteDatabase db;

    public DBCRUDOperations(Context context, boolean webApplicationAvailable) {
        this.webApplicationAvailable = webApplicationAvailable;

        db = context.openOrCreateDatabase("mydb.sqlite", Context.MODE_PRIVATE, null);
        if (DELETE_LOCAL_DB) {
            db.setVersion(0);
            db.execSQL("DROP TABLE " + DB_NAME);
        }

        if (db.getVersion() == 0) {
            db.setVersion(1);
            db.execSQL("CREATE TABLE " + DB_NAME + " (ID INTEGER PRIMARY KEY, NAME TEXT, DESCRIPTION TEXT, EXPIRY INTEGER, DONE INTEGER, FAVOURITE INTEGER, CONTACTS TEXT ,LAENGENGRAD TEXT, BREITENGRAD TEXT, LOCATIONNAME TEXT)");
        }
        if (DELETE_LOCAL_DB) {
            db.execSQL("DELETE FROM " + DB_NAME);
        }

        if(webApplicationAvailable) {
            remoteDBCRUDOperations = new RemoteDBCRUDOperations();
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

                Log.d("SyncedCrud", "remote is activated: " + webApplicationAvailable);

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

                Cursor cursor = db.query(DB_NAME, new String[]{"ID", "NAME", "DESCRIPTION", "EXPIRY", "DONE", "FAVOURITE", "CONTACTS", "LAENGENGRAD", "BREITENGRAD", "LOCATIONNAME"}, null, null, null, null, "ID");
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
                Cursor cursor = db.query(DB_NAME, new String[]{"ID", "NAME", "DESCRIPTION", "EXPIRY", "DONE", "FAVOURITE","CONTACTS", "LAENGENGRAD", "BREITENGRAD", "LOCATIONNAME"}, null, null, null, null, "ID");
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

    @Override
    public void deleteAllTodos() {
        //dont delete local todos
        //maybe new feature in future
    }

    public void deleteAllTodosOnline() {
            remoteDBCRUDOperations.deleteAllTodos();
    }

    public void createItemOnline(Todo todo) {
        remoteDBCRUDOperations.createToDo(todo, result -> {
            //do nothing
        });
    }

    public List<Todo> readAllTodosOnline() {
        List<Todo> todoList = new ArrayList<>();
        remoteDBCRUDOperations.readAllToDos(result -> {
           for (Todo todo : result) {
               createTodoLocal(todo, new CallbackFunction<Todo>() {
                   @Override
                   public void process(Todo result) {
                       Log.d (logger, "Local item created: " + result.getName());
                       todoList.add(result);
                   }
               });
           }
       });

        return todoList;
    }

    private void createTodoLocal(Todo todo, CallbackFunction<Todo> callback) {
        new AsyncTask<Todo, Void, Todo>() {
            @Override
            protected Todo doInBackground(Todo... params) {
                ContentValues values = setContentValuesForTodo(todo);
                long id = db.insertOrThrow(DB_NAME, null, values);
                todo.setId(id);
                return todo;
            }

            @Override
            protected void onPostExecute(Todo todo) {
                callback.process(todo);
            }
        }.execute(todo);
    }

    @NonNull
    private Todo setTodoFromDB(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex("ID"));
        String name = cursor.getString(cursor.getColumnIndex("NAME"));
        String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
        long expiry = cursor.getLong(cursor.getColumnIndex("EXPIRY"));
        boolean done = (cursor.getInt(cursor.getColumnIndex("DONE"))) == 1;
        boolean favourite = (cursor.getInt(cursor.getColumnIndex("FAVOURITE"))) == 1;
        String lat = cursor.getString(cursor.getColumnIndex("LAENGENGRAD"));
        String lng = cursor.getString(cursor.getColumnIndex("BREITENGRAD"));
        String locationName = cursor.getString(cursor.getColumnIndex("LOCATIONNAME"));


        List<String> contacts = new ArrayList<>();
        String contactsFromDb = cursor.getString(cursor.getColumnIndex("CONTACTS"));
        if (!contactsFromDb.equals("")) {
            String[] substrings = contactsFromDb.split(";");
            Collections.addAll(contacts, substrings);
        }

        Todo todo = new Todo(name, description);

        if (!(lat.equals("") && lng.equals("") && locationName.equals(""))) {
            Todo.LatLng latLng = new Todo.LatLng();
            latLng.setLat(Double.valueOf(lat));
            latLng.setLng(Double.valueOf(lng));
            Todo.Location location = new Todo.Location(locationName, latLng);
            todo.setLocation(location);
        }

        todo.setId(id);
        todo.setExpiry(expiry);
        todo.setDone(done);
        todo.setFavourite(favourite);
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
        if(todo.getLocation() != null) {
            values.put("LAENGENGRAD", todo.getLocation().getLatlng().getLat());
            values.put("BREITENGRAD", todo.getLocation().getLatlng().getLng());
            values.put("LOCATIONNAME", todo.getLocation().getName());
        } else {
            values.put("LAENGENGRAD", "");
            values.put("BREITENGRAD", "");
            values.put("LOCATIONNAME", "");
        }
        String contactsForDB = "";
        if (todo.getContacts().size() > 0) {
            for (String contact : todo.getContacts()) {
                contactsForDB += contact + ";";
            }
        }
        values.put("CONTACTS", contactsForDB);

        return values;
    }
}