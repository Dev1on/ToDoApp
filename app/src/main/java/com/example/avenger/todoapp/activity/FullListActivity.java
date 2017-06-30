package com.example.avenger.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.adapter.FullListAdapter;
import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.presenter.FullListPresenter;
import com.example.avenger.todoapp.view.FullListView;

import java.util.ArrayList;

import static android.R.attr.data;

public class FullListActivity extends AppCompatActivity implements FullListView {

    private FullListPresenter presenter;
    private ViewGroup listView;
    private ArrayAdapter<Todo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_list_activity);

        listView = (ViewGroup) findViewById(R.id.list_view_todo_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent showDetailView = new Intent(context, DetailActivity.class);
                showDetailView.putExtra("createItem", true);
                startActivityForResult(showDetailView,1);
            }
        });

        presenter = new FullListPresenter(this, (DBApplication)getApplication());
        presenter.readAllToDosForInit();
    }

    @Override
    public void toggleDone(Todo todo) {
        presenter.updateTodo(todo);
        adapter.sort(Todo.doneComparator);

    }

    @Override
    public void toggleFavourite(Todo todo) {
        presenter.updateTodo(todo);
    }

    @Override
    public void initializeView(ArrayList<Todo> todos) {
        todos.sort(Todo.doneComparator);
        adapter = new FullListAdapter(this, R.layout.full_list_row, todos, this);
        ((ListView)listView).setAdapter(adapter);
    }

    public void updateView(ArrayList<Todo> todos) {
        adapter.clear();
        todos.sort(Todo.doneComparator);
        ((FullListAdapter)adapter).setTodos(todos);
        int i = 0;
        for (Todo todo : todos) {
            ((FullListAdapter) adapter).checkForExpiry((ListView)listView, i, todo);
            i++;
        }
    }

    @Override
    public void displayTodosNotFound() {
        Toast.makeText(this, "Sorry, Todos not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String operation = data.getStringExtra("operation");
        long todoID = data.getLongExtra("todoID", 0);

        switch (operation) {
            case "create":
                presenter.readAllToDosForChanges();
                break;
            case "update":
                presenter.updateTodoWithIDInList(todoID);
                break;
            case "delete":
                presenter.removeTodoWithIDFromList(todoID);
                break;
            case "returned":
                break;
        }
    }

    @Override
    public void startDetail(View v, long id) {
        Context context = v.getContext();
        Intent showTodoDetails = new Intent(context, DetailActivity.class);
        showTodoDetails.putExtra("id", id);
        showTodoDetails.putExtra("createItem", false);
        startActivityForResult(showTodoDetails, 2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort_done) {
            updateView(presenter.getTodos());
            return true;
        } else if(item.getItemId() == R.id.action_sort_date_importance) {
            //TODO sort by date importance
            return true;
        } else if(item.getItemId() == R.id.action_sort_importance_date) {
            //TODO sort by importance date
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }


    }
}
