package com.example.avenger.todoapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.Comparator;

public class FullListActivity extends Fragment implements FullListView, View.OnClickListener {

    private FullListPresenter presenter;
    private ViewGroup listView;
    private ArrayAdapter<Todo> adapter;
    private Comparator sortOrder = Todo.doneComparator;
    private TodosUpdatedInList callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.full_list_activity, container, false);

        listView = (ViewGroup) rootView.findViewById(R.id.list_view_todo_list);

        setHasOptionsMenu(true);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        presenter = new FullListPresenter(this, (DBApplication)getActivity().getApplication());
        presenter.readAllToDosForInit();

        return rootView;
    }

    @Override
    public void toggleDone(Todo todo) {
        presenter.updateTodo(todo);
        adapter.sort(sortOrder);

    }

    @Override
    public void toggleFavourite(Todo todo) {
        presenter.updateTodo(todo);
    }

    @Override
    public void initializeView(ArrayList<Todo> todos) {
        todos.sort(sortOrder);
        adapter = new FullListAdapter(getContext(), R.layout.full_list_row, todos, this);
        ((ListView)listView).setAdapter(adapter);
    }

    public void updateView(ArrayList<Todo> todos) {
        adapter.clear();
        todos.sort(sortOrder);
        ((FullListAdapter)adapter).setTodos(todos);
        int i = 0;
        for (Todo todo : todos) {
            ((FullListAdapter) adapter).checkForExpiry((ListView) listView, i, todo);
            i++;
        }
    }

    @Override
    public void updateMapView(ArrayList<Todo> todos) {
        callback.updateTodosMap(todos);
    }

    public void updateViewAfterChangesInMap(ArrayList<Todo> todos) {
        updateView(todos);
    }

    @Override
    public void displayTodosNotFound() {
        Toast.makeText(getContext(), "Sorry, Todos not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.full_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort_done) {
            setSortOrder(Todo.doneComparator);
            updateView(presenter.getTodos());
            return true;
        } else if (item.getItemId() == R.id.action_sort_date_importance) {
            setSortOrder(Todo.dateImportanceComparator);
            updateView(presenter.getTodos());
            return true;
        } else if (item.getItemId() == R.id.action_sort_importance_date) {
            setSortOrder(Todo.importanceDateComparator);
            updateView(presenter.getTodos());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void setSortOrder(Comparator sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent showDetailView = new Intent(context, DetailActivity.class);
        showDetailView.putExtra("createItem", true);
        startActivityForResult(showDetailView,1);
    }

    public interface TodosUpdatedInList {
        void updateTodosMap(ArrayList<Todo> todos);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity parentActivity;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof Activity){
            parentActivity = (Activity) context;
            try {
                callback = (TodosUpdatedInList) parentActivity;
            } catch (ClassCastException e) {
                throw new ClassCastException(parentActivity.toString()
                        + " must implement TodosUpdated");
            }
        }
    }
}
