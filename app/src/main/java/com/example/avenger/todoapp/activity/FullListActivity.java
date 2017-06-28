package com.example.avenger.todoapp.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.adapter.FullListAdapter;
import com.example.avenger.todoapp.helper.DividerItemDecoration;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.presenter.FullListPresenter;
import com.example.avenger.todoapp.view.FullListView;

public class FullListActivity extends AppCompatActivity implements FullListView {

    private Todo[] todos;
    private FullListPresenter presenter;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_list_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent showDetailView = new Intent(context, DetailActivity.class);
                showDetailView.putExtra("createItem", true);
                context.startActivity(showDetailView);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new FullListAdapter(todos);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(adapter);

        presenter = new FullListPresenter(this);
    }

    @Override
    public void toggleDone() {

    }

    @Override
    public void toggleFavourite() {

    }

    @Override
    public void setToDos(Todo[] todos) {
        this.todos = todos;
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
