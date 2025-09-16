package com.example.todo.service;

import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TodoService {
    public final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    public List<Todo> index() {
        return todoRepository.findAll();
    }
    public Todo create(Todo todo) {
        if("".equals(todo.getText()) || todo.getText() == null){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Text is required");
        }
        return todoRepository.save(todo);
    }
}
