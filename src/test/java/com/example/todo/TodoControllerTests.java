package com.example.todo;

import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(jsonPath("$.id").value(Matchers.not("client-sent")))
                .andExpect(jsonPath("$.text").value("Buy bread"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test

}
