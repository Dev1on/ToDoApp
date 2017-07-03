package com.example.avenger.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.presenter.FullListMapPresenter;
import com.example.avenger.todoapp.view.FullListMapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class FullListMapActivity extends Fragment implements FullListMapView, View.OnClickListener {

    private MapView mMapView;
    private GoogleMap mMap;
    private FullListMapPresenter presenter;
    private TodosUpdatedInMap callback;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.full_list_map_activity, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        presenter = new FullListMapPresenter(this, (DBApplication)getActivity().getApplication());
        presenter.readAllToDosForInit();

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
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
    public void displayTodosNotFound() {
        Toast.makeText(getContext(), "Sorry, Todos not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startDetail(long id) {
        Intent showTodoDetails = new Intent(getContext(), DetailActivity.class);
        showTodoDetails.putExtra("id", id);
        showTodoDetails.putExtra("createItem", false);
        startActivityForResult(showTodoDetails, 2);
    }

    @Override
    public void fillWithTodos(ArrayList<Todo> todos) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.clear();
                Log.i("onMapReady", "beforeTODOS");
                for (Todo todo : todos) {
                    Log.i("onMapReady", "markerAdded");
                    Todo.Location location = todo.getLocation();
                    if(location != null) {
                        LatLng latLng = new LatLng(location.getLatlng().getLat(), location.getLatlng().getLng());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location.getName())).setTag(todo.getId());
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                long todoId = (long) marker.getTag();
                                startDetail(todoId);
                                return false;
                            }
                        });
                    }
                }
            }
        });
    }

    public void updateListView(ArrayList<Todo> todos) {
        callback.updateTodosList(todos);
    }

    public void fillWithTodosAfterChangesInList(ArrayList<Todo> todos) {
        fillWithTodos(todos);
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent showDetailView = new Intent(context, DetailActivity.class);
        showDetailView.putExtra("createItem", true);
        startActivityForResult(showDetailView,1);
    }

    public interface TodosUpdatedInMap {
        void updateTodosList(ArrayList<Todo> todos);
    }
}
