package com.example.qa.user;

import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.exchange.ModifyPasswordAttribute;
import com.example.qa.user.exchange.UserAttribute;
import com.example.qa.user.repository.UserRepository;
import com.example.qa.user.response.GetAllResponse;
import com.example.qa.user.response.GetPermitResponse;
import com.example.qa.user.response.GetUserResponse;
import com.example.qa.user.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.example.qa.user.JsonHelper.fromJson;
import static com.example.qa.user.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;
    private String password = "password";
    @Autowired
    private UserRepository repository;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    @Test
    void login() throws Exception {
        //test for login success
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword(password);
        MvcResult loginResult = this.mockMvc
                .perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = fromJson(objectMapper, loginResult.getResponse().getContentAsString(), LoginResponse.class);
        this.token = response.getToken();
        assertNotNull(response.getToken(), "Token must not be null!");
        assertEquals(response.getUser().getUsername(), "testUser");

        loginRequest.setPassword("pa");
        //test for wrong password login
        this.mockMvc.perform(post("/api/user/login")
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(toJson(objectMapper, loginRequest)))
                                             .andExpect(status().isUnauthorized())
                                             .andReturn();
    }

    @Test
    void getAllUsers() throws Exception {

        //test for getAllUser success
        MvcResult getAllResult = this.mockMvc.perform(get("/api/users")
                                                      .header("Authorization", "Bearer " + token))
                                             .andExpect(status().isOk())
                                             .andReturn();

        GetAllResponse response = fromJson(objectMapper, getAllResult.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(response.getUsers().size(), repository.count());

        //test for not authenticated
        this.mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void getUser() throws Exception{

        //test for success get user detail
        MvcResult getUserResult = this.mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        GetUserResponse response = fromJson(objectMapper, getUserResult.getResponse().getContentAsString(), GetUserResponse.class);
        assertNotNull(response.getAvatarUrl(),"不能为空");
        assertNotNull(response.getBirthday(),"不能为空");
        assertNotNull(response.getUsername(),"不能为空");
        assertNotNull(response.getGender(),"不能为空");
        assertNotNull(response.getId(),"不能为空");
        assertNotNull(response.getMail(),"不能为空");
        assertNotNull(response.getNickname(),"不能为空");
        assertNotNull(response.getSignUpTimestamp(),"不能为空");
        if(repository.findById(1L).isEmpty()){
            throw new Exception("用户不存在");
        }
        var user = repository.findById(1L).get();
        assertEquals(response.getUsername(), user.getUsername(),"用户名不正确");
        assertEquals(response.getAvatarUrl(), user.getAva_url(),"头像路径不正确");
        assertEquals(response.getBirthday(), user.getBirthday(),"生日不正确");
        assertEquals(response.getGender(), user.getGend(),"性别不正确");
        assertEquals(response.getId(), user.getId() ,"id不正确");
        assertEquals(response.getMail(), user.getEmail(), "邮件不正确");
        assertEquals(response.getNickname(), user.getNickname(), "昵称不正确");
        assertEquals(response.getSignUpTimestamp(), user.getSign_up_timestamp(), "时间戳不正确");

        long id = repository.count() + 1;

        //test for not existed user
        this.mockMvc.perform(get("/api/users/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();

        //test for not authenticated
        this.mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void permitQuest() throws Exception{

        //test for success get user permission
        MvcResult getPermissionResult = this.mockMvc.perform(get("/api/users/1/permission")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        GetPermitResponse response = fromJson(objectMapper, getPermissionResult.getResponse().getContentAsString(), GetPermitResponse.class);
        if(repository.findById(1L).isEmpty()){
            throw new Exception("用户不存在");
        }
        var user = repository.findById(1L).get();
        assertEquals(response.getPermit(), user.getPermit());

        //test for get user not existed permission
        long id = repository.count() + 1;
        this.mockMvc.perform(get("/api/users/" + id + "/permission")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();

        //test for not authenticated
        this.mockMvc.perform(get("/api/users/1/permission"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void deleteUser() throws Exception{

        //test for success delete user
        long expected = repository.count() - 1;
        this.mockMvc.perform(delete("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("id", "2"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(expected, repository.count());

        //test for user not existed delete
        this.mockMvc.perform(delete("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("id", "2"))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(expected, repository.count());

        //test for not authenticated
        this.mockMvc.perform(delete("/api/users")
                        .param("id", "1"))
                .andExpect(status().isForbidden())
                .andReturn();
    }


    @Test
    void genAvatar() throws Exception{

        //test getting avatar
        this.mockMvc.perform(get("/api/users/avatar/" + 1 + ".png")
                             .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void register() throws Exception{

        long expected = repository.count() + 1;

        UserAttribute register = new UserAttribute();
        register.setUsername("e");
        register.setPassword("e");

        //test register success
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, register)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expected, repository.count());

        //test register existed username
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, register)))
                .andExpect(status().isForbidden())
                .andReturn();
    }


    @Test
    void modifyUser() throws Exception{
        UserAttribute modify = new UserAttribute();
        modify.setUsername("ee");
        modify.setPassword("ee");
        modify.setBirthday("2040/15/32");
        modify.setDescription("A student");
        modify.setEmail("@mails.tsinghua.edu.cn");
        modify.setGender("male");
        modify.setPhone("1010");
        modify.setNickname("little");

        //test modify success
        this.mockMvc.perform(put("/api/users/2")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, modify)))
                .andExpect(status().isOk())
                .andReturn();

        if(repository.findById(2L).isEmpty()){
            throw new Exception("用户不存在");
        }
        var user = repository.findById(2L).get();

        assertEquals(user.getUsername(), "ee");
        assertEquals(user.getBirthday(), "2040/15/32");
        assertEquals(user.getEmail(), "@mails.tsinghua.edu.cn");
        assertEquals(user.getDescription(), "A student");
        assertEquals(user.getGend(),"male");
        assertEquals(user.getPhone(),"1010");
        assertEquals(user.getNickname(), "little");

        //test modify user not existed
        long id = repository.count() + 1;
        this.mockMvc.perform(put("/api/users/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, modify)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //test not authenticated
        this.mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, modify)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void modifyPass() throws Exception{
        ModifyPasswordAttribute modify = new ModifyPasswordAttribute();
        modify.setOrigin("password");
        modify.setPassword("pass");

        //test success modify password
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, modify)))
                .andExpect(status().isOk())
                .andReturn();
        this.password = "pass";
        login();

        //test wrong origin
        modify.setOrigin("password");
        modify.setPassword("password");
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //test not authenticated
        modify.setOrigin("pass");
        modify.setPassword("password");
        this.mockMvc.perform(put("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //change back to origin
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, modify)))
                .andExpect(status().isOk())
                .andReturn();
        this.password = "password";
    }

}