package com.example.demo.Controllers;

import com.example.demo.Entities.CustomerEntity;
import com.example.demo.Services.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @InjectMocks
    private CustomerController controller;

    @Test
    public void saveUser_ShouldReturnUser() throws Exception {
        CustomerEntity user = new CustomerEntity();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("user@example.com");

        given(customerService.saveUser(Mockito.any())).willReturn(user);

        String json = """
            {
                "name": "Test User",
                "email": "user@example.com",
                "rut": "12345678-9",
                "password": "securepass",
                "phone": "999999999",
                "birthDate": "1990-01-01T00:00:00",
                "monthVisits": 0,
                "admin": false
            }
            """;

        mockMvc.perform(post("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("user@example.com")));
    }

    @Test
    public void saveUser_ShouldReturnBadRequest_WhenUserIsNull() throws Exception {
        given(customerService.saveUser(Mockito.any())).willReturn(null);

        String json = """
            {
                "name": "Duplicate",
                "email": "already@exists.com",
                "rut": "12345678-9",
                "password": "pass",
                "phone": "000000000",
                "birthDate": "1990-01-01T00:00:00",
                "monthVisits": 0,
                "admin": false
            }
            """;

        mockMvc.perform(post("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserById_ShouldReturnUser() throws Exception {
        CustomerEntity user = new CustomerEntity();
        user.setId(1L);
        user.setName("Claudia");

        given(customerService.getUserById(1L)).willReturn(user);

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Claudia")));
    }

    @Test
    public void updateUser_ShouldReturnUpdatedUser() throws Exception {
        CustomerEntity updatedUser = new CustomerEntity();
        updatedUser.setId(1L);
        updatedUser.setName("Updated");

        given(customerService.updateUser(Mockito.any())).willReturn(updatedUser);

        String json = """
            {
                "id": 1,
                "name": "Updated",
                "email": "update@example.com",
                "rut": "12345678-9",
                "password": "newpass",
                "phone": "888888888",
                "birthDate": "1990-01-01T00:00:00",
                "monthVisits": 2,
                "admin": false
            }
            """;

        mockMvc.perform(put("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    public void deleteUserById_ShouldReturnNoContent() throws Exception {
        Mockito.when(customerService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUserByRut_ShouldReturnUser() throws Exception {
        CustomerEntity user = new CustomerEntity();
        user.setRut("12345678-9");
        user.setName("User Rut");

        given(customerService.getUserByRut("12345678-9")).willReturn(user);

        mockMvc.perform(get("/user/rut/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("User Rut")));
    }

    @Test
    public void login_ShouldReturnCustomer_WhenValidCredentials() throws Exception {
        CustomerEntity user = new CustomerEntity();
        user.setId(1L);
        user.setName("Login User");
        user.setEmail("login@example.com");
        user.setRut("12345678-9");

        given(customerService.login(Mockito.any())).willReturn(user);

        String json = """
        {
            "rut": "12345678-9",
            "password": "somepass"
        }
        """;

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("login@example.com")))
                .andExpect(jsonPath("$.rut", is("12345678-9")));
    }

}

