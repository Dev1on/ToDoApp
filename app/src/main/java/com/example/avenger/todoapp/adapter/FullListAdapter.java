package com.example.avenger.todoapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.view.FullListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class FullListAdapter extends ArrayAdapter<Todo> {

    private final ArrayList<Todo> todos = new ArrayList<>();
    private static FullListView fullListView;

    private static final String HH_MM = "HH:mm";
    private static final String DD_MM_YYYY = "dd.MM.yyyy";

    public FullListAdapter(Context context, int resource, ArrayList<Todo> todos, FullListView fullListView) {
        super(context,resource,todos);
        this.todos.addAll(todos);
        FullListAdapter.fullListView = fullListView;
    }

    public static class ViewHolder {

        public long id;
        public TextView name;
        public CheckBox done;
        public CheckBox favourite;
        public TextView date;
        public TextView time;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
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
        DateFormat fullDateFormatter = new SimpleDateFormat(DD_MM_YYYY, Locale.GERMANY);
        DateFormat timeFormatter = new SimpleDateFormat(HH_MM, Locale.GERMANY);
        long dbTime = todo.getExpiry();

        Log.d("adapter","Get view with todo..." + todo.getId());

        resetOnCheckedChanged(viewHolder);

        viewHolder.id = todo.getId();
        viewHolder.name.setText(todo.getName());
        viewHolder.date.setText(fullDateFormatter.format(dbTime));
        viewHolder.time.setText(timeFormatter.format(dbTime));
        viewHolder.done.setChecked(todo.isDone());
        viewHolder.favourite.setChecked(todo.isFavourite());

        // add event listener
        view.setOnClickListener(v -> fullListView.startDetail(v, viewHolder.id));
        viewHolder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todo.setDone(isChecked);
            fullListView.toggleDone(todo);
        });
        viewHolder.favourite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            todo.setFavourite(isChecked);
            fullListView.toggleFavourite(todo);
        });

        if (todo.getExpiry() < System.currentTimeMillis())
            view.setBackgroundColor(0xAAAA0000);
        else
            view.setBackgroundColor(0x00000000);

        return view;
    }

    public void checkForExpiry(ListView listView,int pos ,Todo todo) {
        //check view for expiry
        if (todo.getExpiry() < System.currentTimeMillis())
            getViewByPosition(pos, listView).setBackgroundColor(0xFFFF0000);
        else
            getViewByPosition(pos, listView).setBackgroundColor(0x00000000);
    }

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void resetOnCheckedChanged(ViewHolder viewHolder) {
        viewHolder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {
        });
        viewHolder.favourite.setOnCheckedChangeListener((buttonView, isChecked) -> {
        });
    }

    @Override
    public int getCount() {
        return todos.size();
    }

    @Override
    public Todo getItem(int position) {
        return todos.get(position);
    }

    @Override
    public void clear() {
        super.clear();
        todos.clear();
        notifyDataSetChanged();
    }

    public void setTodos(ArrayList<Todo> todos) {
        this.todos.addAll(todos);
    }
}