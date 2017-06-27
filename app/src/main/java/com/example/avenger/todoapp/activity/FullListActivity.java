package com.example.avenger.todoapp.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

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

    }


    @Override
    public void showDetails() {

    }

    @Override
    public void createItem() {

    }

    @Override
    public void toggleDone() {

    }

    @Override
    public void toggleFavourite() {

    }

    @Override
    public void setToDos() {

    }

    @Override
    public void displayTodosNotFound() {

    }
}
