package com.example.avenger.todoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.activity.DetailActivity;
import com.example.avenger.todoapp.model.Todo;

import org.w3c.dom.Text;

public class FullListAdapter extends RecyclerView.Adapter<FullListAdapter.ViewHolder> {

    private Todo[] todos = new Todo[]{};

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public long id;
        public TextView name;
        public CheckBox done;
        public CheckBox favourite;
        public TextView date;
        public TextView time;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.list_name);
            date = (TextView) v.findViewById(R.id.list_dateText);
            time = (TextView) v.findViewById(R.id.list_timeText);
            done = (CheckBox) v.findViewById(R.id.list_doneBox);
            favourite = (CheckBox) v.findViewById(R.id.list_favouriteBox);
            v.setOnClickListener(this);
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

    public FullListAdapter(Todo[] todos) {
        this.todos = new Todo[todos.length];
        System.arraycopy(todos, 0, this.todos, 0, todos.length);
    }

    @Override
    public FullListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.full_list_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FullListAdapter.ViewHolder holder, int position) {

        Log.i("todos in onBindViewHold", "" + todos.length);

        Todo todo = todos[position];

        holder.id = todo.getId();
        holder.name.setText(todo.getName());
        // TODO time
        // TODO date
        holder.done.setChecked(todo.isDone());
        holder.favourite.setChecked(todo.isFavourite());
    }

    @Override
    public int getItemCount() {
        return todos.length;
    }
}
