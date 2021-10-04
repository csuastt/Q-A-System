package com.example.qa.user;

import com.example.qa.user.repository.UserRepository;
import com.example.qa.user.response.GetAllResponse;
import com.example.qa.user.response.GetPermitResponse;
import com.example.qa.user.response.GetUserResponse;
import com.example.qa.user.response.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import static com.example.qa.user.JsonHelper.fromJson;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private String token;
    @Autowired
    private UserRepository repository;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    @Test
    void setUp() throws Exception {
        MvcResult loginResult = this.mockMvc
                .perform(post("/api/user/login?username=testUser&password=password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = fromJson(objectMapper, loginResult.getResponse().getContentAsString(), LoginResponse.class);
        this.token = response.getToken();
        assertNotNull(response.getToken(), "Token must not be null!");
        assertEquals(response.getUser().getUsername(), "testUser");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllUsers() throws Exception {
        MvcResult getAllResult = this.mockMvc.perform(get("/api/users")
                                                      .header("Authorization", "Bearer " + token))
                                             .andExpect(status().isOk())
                                             .andReturn();

        GetAllResponse response = fromJson(objectMapper, getAllResult.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(response.getUsers().size(), repository.count());
    }

    @Test
    void getUser() throws Exception{
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
    }

    @Test
    void permitQuest() throws Exception{
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
    }

    @Test
    void deleteUser() throws Exception{
    }

    @Test
    void genAvatar() throws Exception{
    }

    @Test
    void register() throws Exception{

    }

    @Test
    void modifyUser() throws Exception{
    }

    @Test
    void modifyPass() throws Exception{
    }
}