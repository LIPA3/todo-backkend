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
        todo.setId(null);
        return todoRepository.save(todo);
    }
    public Todo update(String id, Todo todo) {
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
        if (todo.getText() != null) {
            existingTodo.setText(todo.getText());
        }
        existingTodo.setDone(todo.isDone());
        return todoRepository.save(existingTodo);
    }
}
