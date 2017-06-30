package com.example.avenger.todoapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.avenger.todoapp.R;
import com.example.avenger.todoapp.database.DBApplication;
import com.example.avenger.todoapp.presenter.LoginPresenter;
import com.example.avenger.todoapp.view.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private ProgressBar progressBar;
    private EditText email;
    private EditText password;
    private LoginPresenter presenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        presenter = new LoginPresenter(this, getSystemService(Context.CONNECTIVITY_SERVICE));
        progressDialog = new ProgressDialog(this);



        // set listeners for editText fields
        email = (EditText) findViewById(R.id.loginEmail);
        password = (EditText) findViewById(R.id.loginPassword);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        checkFieldsForEmptyValues();

        // set policy to all due to thread problems
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        // check internet connection and webApplication availability
        if(presenter.isInternetConnectionAvailable() && presenter.isWebApplicationAvailable()) {
            progressBar = (ProgressBar) findViewById(R.id.loginProgress);
            findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    presenter.validateCredentials(email.getText().toString(), password.getText().toString());
                }
            });

            ((DBApplication)getApplication()).setWebApplicationAvailable(true);
        } else {
            ((DBApplication)getApplication()).setWebApplicationAvailable(false);

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("WebApplication not available. Locale database will be used!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    navigateToHome();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setEmailError() {
        email.setError("Invalid Email!");
    }

    @Override
    public void setPasswordError() {
        password.setError("Invalid Password!");
    }

    @Override
    public void setInvalidUserError() {email.setError("Invalid User Credentials!");}

    @Override
    public void navigateToHome() {
        startActivity(new Intent(this, FullListActivity.class));
        finish();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };

    private void checkFieldsForEmptyValues() {
        String emailValue = email.getText().toString();
        String passwordValue = password.getText().toString();

        if(TextUtils.isEmpty(emailValue) || TextUtils.isEmpty(passwordValue)) {
            findViewById(R.id.loginButton).setEnabled(false);
        } else {
            findViewById(R.id.loginButton).setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
