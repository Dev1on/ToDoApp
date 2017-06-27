package com.example.avenger.todoapp.view;

public interface LoginView {
    void showProgress();

    void hideProgress();

    void setEmailError();

    void setPasswordError();

    void navigateToHome();
}
