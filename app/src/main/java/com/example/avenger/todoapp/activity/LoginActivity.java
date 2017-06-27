package com.example.avenger.todoapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        presenter = new LoginPresenter(this, getSystemService(Context.CONNECTIVITY_SERVICE));

        findViewById(R.id.loginButton).setEnabled(false);

        // set policy to all due to thread problems
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // check internet connection and webApplication availability
        if(presenter.isInternetConnectionAvailable() && presenter.isWebApplicationAvailable()) {
            progressBar = (ProgressBar) findViewById(R.id.loginProgress);
            email = (EditText) findViewById(R.id.loginEmail);
            password = (EditText) findViewById(R.id.loginPassword);
            findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    presenter.validateCredentials(email.getText().toString(), password.getText().toString());
                }
            });

            ((DBApplication) getApplication()).setWebApplicationAvailable(true);
        } else {
            Toast.makeText(this, "WebApplication unavailable!", Toast.LENGTH_SHORT).show();

            ((DBApplication) getApplication()).setWebApplicationAvailable(false);
            navigateToHome();
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
    public void navigateToHome() {
        startActivity(new Intent(this, FullListActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
