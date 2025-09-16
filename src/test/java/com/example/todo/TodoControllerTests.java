package com.example.todo;

import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    public void setUp(){
        todoRepository.deleteAll();
    }
    @Test
    void should_response_empty_list_when_index_with_no_any_todo() throws Exception {
        MockHttpServletRequestBuilder request=get("/todos")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
    @Test
    void should_response_one_todo_when_index_with_one_todo() throws Exception {
        Todo todo=new Todo(null,"Buy Milk",false);
        todoRepository.save(todo);
        MockHttpServletRequestBuilder request=get("/todos")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].text").value("Buy Milk"))
                .andExpect(jsonPath("$[0].done").value(false));
    }

    @Test
    void should_response_201_when_create_todo() throws Exception {
        MockHttpServletRequestBuilder request=post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "text":"Buy Milk",
                            "done":false
                        } 
                        """);
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Buy Milk"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void should_response_422_when_create_todo_with_empty_text() throws Exception {
        MockHttpServletRequestBuilder request=post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "text":"",
                            "done":false
                        } 
                        """);
        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void should_response_422_when_create_todo_with_no_text() throws Exception {
        MockHttpServletRequestBuilder request = post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "done": false }
                        """);
        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }
    @Test
    void should_response_201_and_server_generate_id_when_create_todo_with_client_sent_id() throws Exception {
        MockHttpServletRequestBuilder request=post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "id":"client-sent",
                            "text":"Buy bread",
                            "done":false
                        } 
                        """);
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(not("client-sent")))
                .andExpect(jsonPath("$.text").value("Buy bread"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void should_response_200_and_update_both_fields_when_put_with_both_fields() throws Exception {
        Todo todo=new Todo("123","Buy Milk",false);
        todo=todoRepository.save(todo);
        MockHttpServletRequestBuilder request=put("/todos/"+todo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "text":"Buy snacks",
                            "done":true
                        } 
                        """);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Buy snacks"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void should_response_200_and_ignore_id_in_body_when_put_with_surplus_id_field() throws Exception {
        Todo todo1=new Todo("123","Buy Milk",false);
        todo1=todoRepository.save(todo1);
        Todo todo2=new Todo("456","Buy bread",false);
        todoRepository.save(todo2);
        MockHttpServletRequestBuilder request=put("/todos/"+todo1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "id":"456",
                            "text":"Buy snacks",
                            "done":true
                        } 
                        """);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Buy snacks"))
                .andExpect(jsonPath("$.done").value(true));
        Todo updatedTodo1=todoRepository.findById("123").orElseThrow();
        assert updatedTodo1.getText().equals("Buy snacks");
        assert updatedTodo1.isDone();
        Todo updatedTodo2=todoRepository.findById("456").orElseThrow();
        assert updatedTodo2.getText().equals("Buy bread");
        assert !updatedTodo2.isDone();
    }

    @Test
    void should_response_404_when_put_with_non_existing_id() throws Exception {
        MockHttpServletRequestBuilder request=put("/todos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "text":"Buy snacks",
                            "done":true
                        } 
                        """);
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void should_response_422_when_put_with_incomplete_payload() throws Exception {
        Todo todo=new Todo("123","Buy Milk",false);
        todo=todoRepository.save(todo);
        MockHttpServletRequestBuilder request=put("/todos/"+todo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { }
                        """);
        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }
}
