package com.example.avenger.todoapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.activity.DetailActivity;
import com.example.avenger.todoapp.model.Todo;


import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FullListAdapter extends ArrayAdapter<Todo> {

    private static String logger = FullListAdapter.class.getSimpleName();

    private ArrayList<Todo> todos = new ArrayList<>();

    public static final String HH_MM = "HH:mm";
    public static final String DD_MM_YYYY = "dd.MM.yyyy";

    public FullListAdapter(Context context, int resource, ArrayList<Todo> todos) {
        super(context,resource,todos);
        this.todos.addAll(todos);

        Log.i("todos in adapter", "" + todos.size());
    }

    public static class ViewHolder implements View.OnClickListener {

        public long id;
        public TextView name;
        public CheckBox done;
        public CheckBox favourite;
        public TextView date;
        public TextView time;

        public ViewHolder(View view) {
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent showTodoDetails = new Intent(context, DetailActivity.class);
            showTodoDetails.putExtra("id", id);
            showTodoDetails.putExtra("createItem", false);
            context.startActivity(showTodoDetails);
        }
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.i("getView", "called");
        if (view != null) {
            Log.i(logger, "reusing exisiting itemview for element ");
        } else {
            Log.i(logger, "creating new itemView for element at position " + position);
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.full_list_row, null);

            TextView name = (TextView) view.findViewById(R.id.list_name);
            TextView date = (TextView) view.findViewById(R.id.list_dateText);
            TextView time = (TextView) view.findViewById(R.id.list_timeText);
            CheckBox done = (CheckBox) view.findViewById(R.id.list_doneBox);
            CheckBox favourite = (CheckBox) view.findViewById(R.id.list_favouriteBox);

            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.name = name;
            viewHolder.date = date;
            viewHolder.time = time;
            viewHolder.done = done;
            viewHolder.favourite = favourite;

            view.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Todo todo = getItem(position);
        DateFormat fullDateFormatter = new SimpleDateFormat(DD_MM_YYYY);
        DateFormat timeFormatter = new SimpleDateFormat(HH_MM);
        long dbTime = todo.getExpiry();

        viewHolder.id = todo.getId();
        viewHolder.name.setText(todo.getName());
        viewHolder.date.setText(fullDateFormatter.format(dbTime));
        viewHolder.time.setText(timeFormatter.format(dbTime));
        viewHolder.done.setChecked(todo.isDone());
        viewHolder.favourite.setChecked(todo.isFavourite());

        return view;
    }
}