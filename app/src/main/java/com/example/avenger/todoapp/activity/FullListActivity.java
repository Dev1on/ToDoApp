package com.example.avenger.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        adapter = new FullListAdapter(this, R.layout.full_list_row, presenter.getTodos(), this);
        ((ListView)listView).setAdapter(adapter);
    }

    public void updateView(ArrayList<Todo> todos) {
        adapter.clear();
        ((FullListAdapter)adapter).setTodos(todos);
        adapter.notifyDataSetChanged();
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
        presenter.readAllToDosForChanges();
    }

    @Override
    public void startDetail(View v, long id) {
        Context context = v.getContext();
        Intent showTodoDetails = new Intent(context, DetailActivity.class);
        showTodoDetails.putExtra("id", id);
        showTodoDetails.putExtra("createItem", false);
        startActivityForResult(showTodoDetails, 2);
    }
}
