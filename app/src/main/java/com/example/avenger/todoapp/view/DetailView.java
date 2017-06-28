package com.example.avenger.todoapp.view;

import com.example.avenger.todoapp.model.Todo;

public interface DetailView {
        void saveItem();

        void deleteItem();

        void setTodo(Todo todo);

        Todo getCurrentTodo();

        void displayTodoNotFound();
}
