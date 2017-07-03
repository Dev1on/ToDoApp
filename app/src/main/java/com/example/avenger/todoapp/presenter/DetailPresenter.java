package com.example.avenger.todoapp.presenter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.database.ICRUDOperationsAsync;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.view.DetailView;

import java.util.ArrayList;

public class DetailPresenter {

    private DetailView detailView;
    private final ICRUDOperationsAsync crudOperations;
    private Todo todo;

    public DetailPresenter(DetailView detailView, DBApplication application) {
        this.detailView = detailView;
        crudOperations = application.getCrudOperations();
    }

    public void createItem() {
        Todo newTodo = detailView.getCurrentTodo();
        crudOperations.createToDo(newTodo, result -> {
            setTodo(result);
            detailView.setTodo(result);
        });
    }

    public void saveItem() {
        Todo newTodo = detailView.getCurrentTodo();

        crudOperations.updateToDo(todo.getId(), newTodo, result -> {
            setTodo(result);
            detailView.setTodo(result);
        });
    }

    public void readToDo(long id) {
        crudOperations.readToDo(id, result -> {
            Log.d("DetailPresenter", "Result is: " + result);

            if(null == result || result.getId() == 0) {
                detailView.displayTodoNotFound();
            } else {
                setTodo(result);
                detailView.setTodo(result);
            }
        });
    }

    public void deleteToDo(long id) {
        crudOperations.deleteToDo(id, result -> Log.d("DetailPresenter", "Removed todo with ID: " + id));
    }

    private void setTodo(Todo todo) {
        this.todo = todo;
    }

    public void onDestroy() {
        detailView = null;
    }

    public String getContactName(Uri data, ContentResolver resolver) {
        Cursor cursor = resolver.query(data, null, null,null,null);
        cursor.moveToNext();

        String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        cursor.close();

        return contactName;
    }

    public ArrayList<String> getListOfPhoneNumbersForContact(ContentResolver resolver, String contact) {
        Cursor contactsCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null,null);

        ArrayList<String> allPhoneNumbersForContact = new ArrayList<>();
        if(contactsCursor.moveToFirst())
        {
            do
            {
                //get current contact in cursor
                String curName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //if contact equals contact in cursor then get phoneNumbers
                if(contact.equals(curName)) {
                    if (Integer.parseInt(contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor phoneNumberCursor =  resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.Contacts.DISPLAY_NAME +" = ?",new String[]{ curName }, null);
                        while (phoneNumberCursor.moveToNext()) {
                            String contactNumber = phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            allPhoneNumbersForContact.add(contactNumber);
                            break;
                        }
                        phoneNumberCursor.close();
                    }
                    break;
                }
            } while (contactsCursor.moveToNext()) ;
        }
        contactsCursor.close();
        return allPhoneNumbersForContact;
    }

    public ArrayList<String> getListOfEmailsForContact(ContentResolver resolver, String contact) {
        Cursor contactsCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null,null);

        ArrayList<String> allPhoneNumbersForContact = new ArrayList<>();
        if(contactsCursor.moveToFirst())
        {
            do
            {
                //get current contact in cursor
                String curName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //if contact equals contact in cursor then get phoneNumbers
                if(contact.equals(curName)) {
                    if (Integer.parseInt(contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor emailCursor =  resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.Contacts.DISPLAY_NAME +" = ?",new String[]{ curName }, null);
                        while (emailCursor.moveToNext()) {
                            String contactNumber = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            allPhoneNumbersForContact.add(contactNumber);
                            break;
                        }
                        emailCursor.close();
                    }
                    break;
                }
            } while (contactsCursor.moveToNext()) ;
        }
        contactsCursor.close();
        return allPhoneNumbersForContact;
    }
}
