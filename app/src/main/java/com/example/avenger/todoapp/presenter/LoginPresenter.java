package com.example.avenger.todoapp.presenter;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.example.avenger.todoapp.model.User;
import com.example.avenger.todoapp.view.LoginView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public class LoginPresenter {

    private static String WEB_APPLICATION_URL = "http:/192.168.43.95:8080/";

    private LoginView loginView;
    private Object systemService;
    private URL webApplicationURL = null;
    private LoginWebAPI webAPI;


    public LoginPresenter(LoginView loginView, Object systemService) {
        createWebApplicationURL();
        this.loginView = loginView;
        this.systemService = systemService;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEB_APPLICATION_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webAPI = retrofit.create(LoginWebAPI.class);
    }

    public void validateCredentials(String email, String password) {
        if(loginView != null) {
            loginView.showProgress();
        }
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
            //5 seconds timeout
            connection.setConnectTimeout(5000);
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
        if(authUser(email, password)) {
            onSuccess();
        } else {
            onInvalidLogin();
        }
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

    private void onInvalidLogin() {
        if (loginView != null) {
            loginView.setInvalidUserError();
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


    private Boolean authUser (String email, String pw) {
        try {
            return webAPI.authUser(new User(email, pw)).execute().body();
        } catch (IOException e) {
            Log.d("LoginPresenter", "Login credentials are wrong!");
            e.printStackTrace();
        }
        return false;
    }

    // inner Interface to define webAPI login on web application
    public interface LoginWebAPI {
        @PUT("/api/users/auth")
        Call<Boolean> authUser(@Body User user);
    }
}
