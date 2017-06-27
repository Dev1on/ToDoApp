package com.example.avenger.todoapp.presenter;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.example.avenger.todoapp.view.LoginView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginPresenter {

    private static String WEB_APPLICATION_URL = "http://127.0.0.1:8080/todos";

    private LoginView loginView;
    private Object systemService;
    private URL webApplicationURL = null;

    public LoginPresenter(LoginView loginView, Object systemService) {
        createWebApplicationURL();
        this.loginView = loginView;
        this.systemService = systemService;
    }

    public void validateCredentials(String email, String password) {
        if(loginView != null) {
            loginView.showProgress();
        }

        // TODO add email validation
        login(email, password);
    }

    public boolean isInternetConnectionAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) systemService;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public boolean isWebApplicationAvailable() {
        HttpURLConnection connection;
        String response;

        try {
            connection = (HttpURLConnection) webApplicationURL.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(150000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            response = connection.getInputStream().toString();
        } catch (IOException e) {
            Log.d("LoginPresenter", "WebApplication unavailable.");
            e.printStackTrace();
            return false;
        }

        if(("").equals(response)) {
            Log.d("LoginPresenter", "WebApplication available, but no response.");
            return false;
        }

        return true;
    }

    private void login(String email, String password) {
        if(TextUtils.isEmpty(email)) {
            onEmailError();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() != 6) {
            onPasswordError();
            return;
        }

        // TODO send to server and validate success
        onSuccess();
    }

    public void onSuccess() {
        if(loginView != null) {
            loginView.navigateToHome();
        }
    }

    public void onEmailError() {
        if(loginView != null) {
            loginView.setEmailError();
            loginView.hideProgress();
        }
    }

    public void onPasswordError() {
        if(loginView != null) {
            loginView.setPasswordError();
            loginView.hideProgress();
        }
    }

    public void onDestroy() {
        loginView = null;
    }

    private void createWebApplicationURL() {
        try {
            webApplicationURL = new URL(WEB_APPLICATION_URL);
        } catch (MalformedURLException e) {
            Log.d("LoginPresenter", "Invalid URL");
            e.printStackTrace();
        }
        Log.d("LoginPresenter", "WebApplicationURL: " + webApplicationURL.toString());
    }
}
