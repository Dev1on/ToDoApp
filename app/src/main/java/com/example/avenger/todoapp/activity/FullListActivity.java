package com.example.avenger.todoapp.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.adapter.FullListAdapter;
import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.helper.DividerItemDecoration;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.presenter.FullListPresenter;
import com.example.avenger.todoapp.view.FullListView;

import java.util.ArrayList;
import java.util.List;

public class FullListActivity extends AppCompatActivity implements FullListView {

    private static String logger = FullListActivity.class.getSimpleName();

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
                startActivityForResult(showDetailView, 1);
            }
        });

        Log.i("beforePresenter", "......");
        presenter = new FullListPresenter(this, (DBApplication)getApplication());
        presenter.readAllToDos();
    }

    @Override
    public void toggleDone() {

    }

    @Override
    public void toggleFavourite() {

    }

    @Override
    public void initializeView(ArrayList<Todo> todos) {
        Log.i("initView", "......");
        Log.i("tod size", "" + todos.size());
        adapter = new FullListAdapter(this, R.layout.full_list_row, presenter.getTodos());
        ((ListView)listView).setAdapter(adapter);
        adapter.setNotifyOnChange(true);

        Log.i("beforeOnClick","");
        ((ListView) listView).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("itemclicke" , "das");
                Context context = view.getContext();
                Intent showTodoDetails = new Intent(context, DetailActivity.class);
                showTodoDetails.putExtra("id", adapter.getItem(position).getId());
                showTodoDetails.putExtra("createItem", false);
                context.startActivity(showTodoDetails);
            }
        });
        Log.i("count adapter", "" + adapter.getCount());
    }

    @Override
    public void displayTodosNotFound() {
        Toast.makeText(this, "Sorry, Todos not found", Toast.LENGTH_SHORT).show();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
