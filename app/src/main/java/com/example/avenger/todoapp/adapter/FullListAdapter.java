package com.example.avenger.todoapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.view.FullListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FullListAdapter extends ArrayAdapter<Todo> {

    private ArrayList<Todo> todos = new ArrayList<>();
    private static FullListView fullListView;

    public static final String HH_MM = "HH:mm";
    public static final String DD_MM_YYYY = "dd.MM.yyyy";

    public FullListAdapter(Context context, int resource, ArrayList<Todo> todos, FullListView fullListView) {
        super(context,resource,todos);
        this.todos.addAll(todos);
        this.fullListView = fullListView;
    }

    public static class ViewHolder {

        public long id;
        public TextView name;
        public CheckBox done;
        public CheckBox favourite;
        public TextView date;
        public TextView time;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.i("getView", "called");

        // get the data item for this position
        Todo todo = getItem(position);
        // check if existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if(view == null) {
            // if there is no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.full_list_row, parent, false);

            viewHolder.name = (TextView) view.findViewById(R.id.list_name);
            viewHolder.date = (TextView) view.findViewById(R.id.list_dateText);
            viewHolder.time = (TextView) view.findViewById(R.id.list_timeText);
            viewHolder.done = (CheckBox) view.findViewById(R.id.list_doneBox);
            viewHolder.favourite = (CheckBox) view.findViewById(R.id.list_favouriteBox);

            // cache the viewHolder object inside the fresh view
            view.setTag(viewHolder);
        } else {
            // view is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) view.getTag();
        }

        // populate the data from the data object via the viewHolder
        DateFormat fullDateFormatter = new SimpleDateFormat(DD_MM_YYYY);
        DateFormat timeFormatter = new SimpleDateFormat(HH_MM);
        long dbTime = todo.getExpiry();

        viewHolder.id = todo.getId();
        viewHolder.name.setText(todo.getName());
        viewHolder.date.setText(fullDateFormatter.format(dbTime));
        viewHolder.time.setText(timeFormatter.format(dbTime));
        viewHolder.done.setChecked(todo.isDone());
        viewHolder.favourite.setChecked(todo.isFavourite());

        // add event listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullListView.startDetail(v, viewHolder.id);
            }
        });

        viewHolder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                todo.setDone(isChecked);
                fullListView.toggleDone(todo);
            }
        });

        viewHolder.favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                todo.setFavourite(isChecked);
                fullListView.toggleFavourite(todo);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return todos.size();
    }

    @Override
    public Todo getItem(int position) {
        return todos.get(position);
    }
}