package com.example.avenger.todoapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.activity.DetailActivity;
import com.example.avenger.todoapp.view.DetailView;

import java.util.ArrayList;

import static android.R.attr.data;
import static android.R.attr.phoneNumber;
import static android.R.id.message;
import static android.support.v4.content.ContextCompat.startActivity;

public class ContactAdapter extends ArrayAdapter<String> {

    private ArrayList<String> contacts;
    private DetailView detailView;

    public ContactAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<String> contacts, DetailView detailView) {
        super(context, resource, contacts);
        this.contacts = contacts;
        this.detailView = detailView;

        Log.d("ContactsAdapter","Adapter created...");
    }

    public static class ViewHolder {
        public TextView name;
        public ImageButton actionSend;
        public ImageButton actionDelete;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        String contact = contacts.get(position);

        ViewHolder viewHolder;
        if (view == null) {
            Log.d("ContactAdapter","Create new View");

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.detail_row_contacts, parent, false);

            viewHolder.name = (TextView) view.findViewById(R.id.contact_name);
            viewHolder.actionSend = (ImageButton) view.findViewById(R.id.contact_row_action_send);
            viewHolder.actionDelete = (ImageButton) view.findViewById(R.id.contact_row_action_delete);

            view.setTag(viewHolder);
        } else {
            Log.d("ContactAdapter","Reuse View");

            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(contact);
        viewHolder.actionSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display message with two buttons: sms and email
                AlertDialog.Builder builder = new AlertDialog.Builder(((DetailActivity) detailView));
                builder.setMessage("Do you want to send a sms or email?")
                        .setCancelable(true)
                        .setPositiveButton("SMS", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //set contact in detailView and start requesting process
                                ((DetailActivity)detailView).setContact(contact);
                                ((DetailActivity)detailView).startSMSProcess();
                            }
                        })
                        .setNegativeButton("eMail", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //if email clicked
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        viewHolder.actionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> newContacts = new ArrayList<String>();
                for (String con : contacts) {
                    if (!con.equals(contact))
                        newContacts.add(con);
                }
                contacts = newContacts;
                Log.d("Contact deleted", "remaining: " + contacts);
                ((DetailActivity)detailView).refreshContactList(contacts);
            }
        });


        return view;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }
}
