package com.example.avenger.todoapp.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.avenger.todoapp.model.Todo;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RemoteDBCRUDOperations implements ICRUDOperationsAsync {

    private static String WEB_API_BASE_URL = "http:/192.168.43.95:8080/";
    private static final String logger = RemoteDBCRUDOperations.class.getSimpleName();

    private final ICRUDOperationsWebAPI webAPI;

    public RemoteDBCRUDOperations() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEB_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webAPI = retrofit.create(ICRUDOperationsWebAPI.class);
    }

    @Override
    public void createToDo(Todo todo, CallbackFunction<Todo> callback) {
        new AsyncTask<Todo, Void, Todo>() {
            @Override
            protected Todo doInBackground(Todo... params) {
                try {
                    Log.d("RemoteCRUD","Jo angelegt.");
                    return webAPI.createToDoItem(todo).execute().body();
                } catch (IOException e) {
                    Log.d(logger, "Item not created.");
                }
                return null;
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
                try {
                    return webAPI.readAllToDoItems().execute().body();
                } catch (IOException e) {
                    Log.d(logger, "Could not read all items.");
                    e.printStackTrace();
                }
                return null;
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
                try {
                    return webAPI.readToDoItem(params[0]).execute().body();
                } catch (IOException e) {
                    Log.d(logger, "Could not read item with id: " + params[0]);
                    e.printStackTrace();
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
    public void updateToDo(long id, Todo todo, CallbackFunction<Todo> callback) {
        new AsyncTask<Long, Void, Todo>() {
            @Override
            protected Todo doInBackground(Long... params) {
                try {
                    return webAPI.updateToDoItem(todo.getId(), todo).execute().body();
                } catch (IOException e) {
                    Log.d(logger, "Could not update todo with id: " + params[0]);
                    e.printStackTrace();
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
                try {
                    return webAPI.deleteToDoItem(params[0]).execute().body();
                } catch (IOException e) {
                    Log.d(logger, "Could not delete item with id: " + params[0]);
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                callback.process(aBoolean);
            }
        }.execute(id);
    }

    @Override
    public void deleteAllTodos() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return webAPI.deleteAllTodos().execute().body();
                } catch (IOException e) {
                    Log.d(logger, "Not all Todos in web application could be deleted.");
                    e.printStackTrace();
                }
                return false;
            }
        }.execute();
    }


    public ICRUDOperationsWebAPI getWebAPI() {
        return webAPI;
    }

    // inner Interface to define webAPI calls to given remote database application
    public interface ICRUDOperationsWebAPI {
        @POST("/api/todos")
        Call<Todo> createToDoItem(@Body Todo item);

        @GET("/api/todos")
        Call<List<Todo>> readAllToDoItems();

        @GET("/api/todos/{id}")
        Call<Todo> readToDoItem(@Path("id") long id);

        @PUT("/api/todos/{id}")
        Call<Todo> updateToDoItem(@Path("id") long id, @Body Todo item);

        @DELETE("/api/todos/{id}")
        Call<Boolean> deleteToDoItem(@Path("id") long id);

        @DELETE("/api/todos")
        Call<Boolean> deleteAllTodos();
    }
}
