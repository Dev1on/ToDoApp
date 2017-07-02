package com.example.avenger.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class FullListMapActivity extends Fragment implements FullListMapView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private MapView mMapView;
    private GoogleMap mMap;
    private FullListMapPresenter presenter;
    private ArrayList<Todo> todos;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.full_list_map_activity, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        presenter = new FullListMapPresenter(this, (DBApplication)getActivity().getApplication());
        presenter.readAllToDosForInit();

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void updateView(ArrayList<Todo> todos) {

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
    public void setTodos(ArrayList<Todo> todos) {
        this.todos = todos;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer todoId = (Integer) marker.getTag();
        startDetail(todoId);

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        for (Todo todo : todos) {
            Todo.Location location = todo.getLocation();
            LatLng latLng = new LatLng(location.getLatlng().getLat(), location.getLatlng().getLng());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location.getName())).setTag(todo.getId());
            mMap.setOnMarkerClickListener(this);
        }
    }
}
