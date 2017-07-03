package com.example.avenger.todoapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.adapter.ContactAdapter;
import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.model.Todo;
import com.example.avenger.todoapp.presenter.DetailPresenter;
import com.example.avenger.todoapp.view.DetailView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.avenger.todoapp.R.color.todo;

public class DetailActivity extends AppCompatActivity implements DetailView, OnMapReadyCallback {

    private static final String HH_MM = "HH:mm";
    private static final String DD_MM_YYYY = "dd.MM.yyyy";
    private static final int PICK_CONTACT_REQ_CODE = 20;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int START_MAP_ACTIVITY = 200;

    private DetailPresenter presenter;
    private ArrayAdapter<String> adapter;
    private ViewGroup listView;

    private TextView idText;
    private EditText nameText;
    private EditText descriptionText;
    private CheckBox favouriteBox;
    private CheckBox doneBox;
    private EditText locationText;
    private EditText dateText;
    private EditText timeText;
    private GoogleMap mMap;
    private MarkerOptions marker;

    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialogBuilder;

    private boolean createItem = false;
    private String contact;
    private String smsOrEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        presenter = new DetailPresenter(DetailActivity.this, ((DBApplication) getApplication()));
        progressDialog = new ProgressDialog(DetailActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);

        idText = (TextView) findViewById(R.id.idTextDetail);
        nameText = (EditText) findViewById(R.id.nameTextDetail);
        descriptionText = (EditText) findViewById(R.id.descriptionTextDetail);
        favouriteBox = (CheckBox) findViewById(R.id.favouriteBoxDetail);
        doneBox = (CheckBox) findViewById(R.id.doneBoxDetail);
        listView = (ViewGroup) findViewById(R.id.contactTextDetail);
        locationText = (EditText) findViewById(R.id.locationTextDetail);
        dateText = (EditText) findViewById(R.id.dateTextDetail);
        timeText = (EditText) findViewById(R.id.timeTextDetail);
        Button addContact = (Button) findViewById(R.id.addContactBtn);
        ImageButton deleteLocationButton = (ImageButton) findViewById(R.id.action_delete_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(DetailActivity.this);


        setSupportActionBar(toolbar);
        setCalendar();
        setDeleteAlert();

        createItem = (boolean) getIntent().getSerializableExtra("createItem");

        if (createItem)
            initializeEmpty();
        else
            initializeScreenWithTodo();

        addContact.setOnClickListener(v -> {
            //start new contacts intent
            Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(pickContactIntent, PICK_CONTACT_REQ_CODE);
        });
        listView.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        deleteLocationButton.setOnClickListener(v -> {
            Todo todo = getCurrentTodo();
            todo.setLocation(null);
            setTodo(todo);
        });

    }

    @Override
    public void saveItem() {
        if (isMandatoryMaintained()) {
            String operation;

            if (!createItem) {
                progressDialog.show();
                presenter.saveItem();
                operation = "update";
            } else {
                presenter.createItem();
                operation = "create";
                createItem = false;
            }

            progressDialog.dismiss();
            Toast.makeText(this, "Todo saved", Toast.LENGTH_SHORT).show();

            Intent returnIntent = getIntent();
            setResult(RESULT_OK, returnIntent);
            returnIntent.putExtra("operation", operation);
            if(TextUtils.equals(operation, "update")) {
                returnIntent.putExtra("todoID", getCurrentTodo().getId());
            }
            finish();
        }
    }

    @Override
    public void deleteItem() {
        presenter.deleteToDo((long) getIntent().getSerializableExtra("id"));
        Toast.makeText(this, "Todo deleted", Toast.LENGTH_SHORT).show();

        Intent returnIntent = getIntent();
        returnIntent.putExtra("operation", "delete");
        returnIntent.putExtra("todoID", getCurrentTodo().getId());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void setTodo(Todo todo) {
        idText.setText("" + todo.getId());
        nameText.setText(todo.getName());
        descriptionText.setText(todo.getDescription());

        DateFormat fullDateFormatter = new SimpleDateFormat(DD_MM_YYYY, Locale.GERMANY);
        DateFormat timeFormatter = new SimpleDateFormat(HH_MM, Locale.GERMANY);
        long dbTime = todo.getExpiry();
        String showDate = fullDateFormatter.format(dbTime);
        String showTime = timeFormatter.format(dbTime);
        if (todo.getExpiry() > 0) {
            dateText.setText(showDate);
            timeText.setText(showTime);
        }
        favouriteBox.setChecked(todo.isFavourite());
        doneBox.setChecked(todo.isDone());

        adapter = new ContactAdapter(this, R.layout.full_list_row, (ArrayList<String>) todo.getContacts(), this);
        ((ListView)listView).setAdapter(adapter);

        if (todo.getLocation() != null) {
            locationText.setText(todo.getLocation().getName());
        } else {
            locationText.setText("");
        }

        progressDialog.dismiss();

        setMapLocation(todo);
    }

    @Override
    public Todo getCurrentTodo() {
        long id = Long.parseLong(idText.getText().toString());
        String name = nameText.getText().toString();
        String description = descriptionText.getText().toString();
        boolean favourite = favouriteBox.isChecked();
        boolean done = doneBox.isChecked();

        //date
        String dateString = dateText.getText().toString();
        String timeString = timeText.getText().toString();
        DateFormat formatter = new SimpleDateFormat(DD_MM_YYYY + ":" + HH_MM, Locale.GERMANY);
        Date date = null;
        try {
            date = formatter.parse(dateString + ":" + timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateAsLong = 0;
        if (date != null)
            dateAsLong = date.getTime();

        ArrayList<String> contacts = new ArrayList<>();
        contacts.addAll(((ContactAdapter)adapter).getContacts());

        Todo returnTodo = new Todo(name, description);

        if(marker != null) {
            Todo.Location location = new Todo.Location();
            Todo.LatLng latlng = new Todo.LatLng();
            latlng.setLat(marker.getPosition().latitude);
            latlng.setLng(marker.getPosition().longitude);
            location.setLatlng(latlng);
            location.setName(locationText.getText().toString());
            returnTodo.setLocation(location);
        }
        returnTodo.setId(id);
        returnTodo.setFavourite(favourite);
        returnTodo.setDone(done);
        if(dateAsLong != 0)
            returnTodo.setExpiry(dateAsLong);
        returnTodo.setContacts(contacts);
        return returnTodo;
    }

    @Override
    public void displayTodoNotFound() {
        Toast.makeText(this, "Sorry, Todo not found", Toast.LENGTH_SHORT).show();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        if(createItem) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            alertDialogBuilder.show();
            return true;
        } else if(item.getItemId() == R.id.action_save) {
            saveItem();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("operation", "returned");
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }

    private void initializeEmpty() {
        Todo todo = new Todo("","");
        todo.setContacts(new ArrayList<>());
        marker = null;

        setTodo(todo);
    }

    private void initializeScreenWithTodo() {
        progressDialog.show();
        long itemId = (long) getIntent().getSerializableExtra("id");
        presenter.readToDo(itemId);

        progressDialog.hide();
    }

    private void setCalendar() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, monthOfYear);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel(cal);
        };
        dateText.setOnClickListener(v -> new DatePickerDialog(DetailActivity.this, date, cal
                .get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show());

        TimePickerDialog.OnTimeSetListener time = (view, hourOfDay, minute) -> {
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            updateTimeLabel(cal);
        };
        timeText.setOnClickListener(v -> new TimePickerDialog(DetailActivity.this, time,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true).show());
    }

    private void updateDateLabel(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY, Locale.GERMANY);
        dateText.setText(sdf.format(cal.getTime()));
        dateText.setError(null);
    }

    private void updateTimeLabel(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(HH_MM, Locale.GERMANY);
        timeText.setText(sdf.format(cal.getTime()));
        timeText.setError(null);
    }

    private void setDeleteAlert() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    deleteItem();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //Do nothing
                    break;
            }
        };
        alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        alertDialogBuilder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
    }

    private boolean isMandatoryMaintained() {
        if (TextUtils.isEmpty(nameText.getText().toString()) ||
                TextUtils.isEmpty(dateText.getText().toString()) ||
                TextUtils.isEmpty(timeText.getText().toString())) {
            if (nameText.getText().toString().trim().equals("")) {
                nameText.setError("Name is required!");
                nameText.setHint("please enter a name");
            }
            if (dateText.getText().toString().trim().equals("")) {
                dateText.setError("Date is required!");
                dateText.setHint("please enter a date");
            }
            if (timeText.getText().toString().trim().equals("")) {
                timeText.setError("Date is required!");
                timeText.setHint("please enter a time");
            }
            return false;
        } else {
            return true;
        }

    }

    public void refreshContactList(ArrayList<String> contacts) {
        adapter = new ContactAdapter(this, R.layout.full_list_row, contacts, this);
        ((ListView)listView).setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQ_CODE && resultCode == RESULT_OK) {
            processSelectedContact(data.getData());
        } else if (requestCode == START_MAP_ACTIVITY && resultCode == RESULT_OK) {
            processSelectedLocation(data.getSerializableExtra("location"));
        }
    }

    private void processSelectedLocation(Serializable location) {
        String locationName = ((Todo.Location)location).getName();
        double lat = ((Todo.Location)location).getLatlng().getLat();
        double lng = ((Todo.Location)location).getLatlng().getLng();
        Todo.LatLng newLatLng = new Todo.LatLng();
        newLatLng.setLat(lat);
        newLatLng.setLng(lng);

        Todo todo = getCurrentTodo();
        todo.setLocation(new Todo.Location(locationName, newLatLng));

        setTodo(todo);
    }

    private void processSelectedContact(Uri data) {
        String name = presenter.getContactName(data, getContentResolver());
        Todo todo = getCurrentTodo();
        todo.getContacts().add(name);
        setTodo(todo);
    }

    public void startSMSProcess() {
        //ask for permission to read all contacts
        this.smsOrEmail = "sms";
        askForPermission(smsOrEmail);
    }

    private void startSmsActivity() {
        ArrayList<String> numbers = presenter.getListOfPhoneNumbersForContact(getContentResolver(), contact);
        if (numbers.size() > 0) {
            String message = getMessage();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + numbers.get(0)));
            intent.putExtra("sms_body", message);
            startActivity(intent);
        } else {
            showContactError();
        }
    }

    private void startEmailActivity() {
        ArrayList<String> emails = presenter.getListOfEmailsForContact(getContentResolver(), contact);
        if (emails.size() > 0) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emails.get(0)});
            i.putExtra(Intent.EXTRA_SUBJECT, "MAD ToDo App: new Todo for you!");
            i.putExtra(Intent.EXTRA_TEXT   , getMessage());
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(DetailActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            showContactError();
        }
    }

    private String getMessage() {
        Todo todo = getCurrentTodo();
        return "Hey, here is a Todo for you:\n" + "Title: " + todo.getName() + "\n" +
                "Description: " + todo.getDescription() + "\n\n" + "Happy solving!!";
    }

    public void startEmailProcess() {
        this.smsOrEmail = "email";
        askForPermission(smsOrEmail);
    }

    private void askForPermission(String smsOrMail) {
        // Check the SDK version and whether the permission is already granted or not.
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            if (smsOrMail.equals("sms"))
                startSmsActivity();
            else if (smsOrMail.equals("email"))
                startEmailActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askForPermission(smsOrEmail);
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot create a sms or email for you.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    private void showContactError() {
        Toast.makeText(this, "No number or email or contact found...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> startNewMapActivity());
    }

    private void startNewMapActivity() {
        Intent selectNewLocationActivity = new Intent(DetailActivity.this, DetailMapsActivity.class);

        if (marker != null) {
            selectNewLocationActivity.putExtra("location", getCurrentTodo().getLocation());
        }
        startActivityForResult(selectNewLocationActivity, START_MAP_ACTIVITY);
    }

    private void setMapLocation(Todo todo) {
        if (todo.getLocation() != null) {
            LatLng googleLatLng = new LatLng(todo.getLocation().getLatlng().getLat(), todo.getLocation().getLatlng().getLng());

            if (mMap != null) {
                mMap.clear();
                marker = new MarkerOptions().position(googleLatLng).title(todo.getLocation().getName());
                mMap.addMarker(marker);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(googleLatLng));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        } else {
            if (mMap != null) {
                marker = null;
                mMap.clear();
            }
        }
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}