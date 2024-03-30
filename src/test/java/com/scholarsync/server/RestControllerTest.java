package com.scholarsync.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholarsync.server.dtos.UserDTO;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.time.LocalDate;

/**
 * To run this tests is important to put hibernate into update mode
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    FriendRequestRepository friendRequestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    GroupRepository groupRepository;


    @Test
    public void registerUserTest() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.parse("1990-01-01"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void loginUserTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void registerRobertTest() throws Exception {
        User user = new User();
        user.setFirstName("Robert");
        user.setLastName("Smith");
        user.setEmail("robertSmith@gmail.com");
        user.setPassword("password123");
        user.setUsername("robertsmith");
        user.setBirthDate(LocalDate.parse("1990-01-01"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }



    @Test
    public void sendFriendRequestTest() throws Exception {

        String robertId = mockMvc.perform(MockMvcRequestBuilders.post("/users/get-id-by-username")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"robertsmith\"}"))
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(MockMvcRequestBuilders.post("/users/send-friend-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"from_id\":\"" + robertId + "\",\"toName\":\"John\",\"to_username\":\"robertsmith\"}")).
                andExpect(MockMvcResultMatchers.content().string("friend-request/sent"));
    }


    /**
     * this test will delete all the data from the database
     */
    @Test
    public void deleteAll(){
        friendRequestRepository.deleteAll();
        sessionRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
        Assertions.assertTrue(true);
    }

}