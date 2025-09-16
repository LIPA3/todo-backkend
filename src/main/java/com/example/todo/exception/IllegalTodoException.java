package com.example.todo.exception;

public class IllegalTodoException extends RuntimeException {
    public IllegalTodoException(String message) {
        super(message);
    }

}
