package com.example.qa.user;

import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.exchange.ModifyPasswordAttribute;
import com.example.qa.user.exchange.UserAttribute;
import com.example.qa.user.model.AppUser;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collection;

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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
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

    void generateUser(String username){
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new AppUser(username, passwordEncoder.encode("password"),authorities);
        repository.save(user);
    }

    void generateAnswerer(String username){
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new AppUser(username, passwordEncoder.encode("password"),authorities);
        user.setPermit("a");
        repository.save(user);
    }

    @Test
    void getAllUsers() throws Exception {

        //init testUsers
        for(int i = 0; i < 40; i++){
            generateUser("user" + i);
        }
        for(int i = 0; i < 20; i++){
            generateAnswerer("answerer" + i);
        }

        //test for default values
        MvcResult getAllResult = this.mockMvc.perform(get("/api/users")
                                                      .header("Authorization", "Bearer " + token))
                                             .andExpect(status().isOk())
                                             .andReturn();

        GetAllResponse response = fromJson(objectMapper, getAllResult.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(response.getUsers().size(), 20);
        assertEquals(response.getUsers().get(0).getId(), 1);

        //test for page feature

        MvcResult page_one = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "1")
                        .param("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_one = fromJson(objectMapper, page_one.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_one.getUsers().size(), 30);
        assertEquals(res_one.getUsers().get(0).getId(), 1);
        assertEquals(res_one.getUsers().get(29).getId(), 30);

        MvcResult page_two = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "2")
                        .param("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_two = fromJson(objectMapper, page_two.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_two.getUsers().size(), 30);
        assertEquals(res_two.getUsers().get(0).getId(), 31);
        assertEquals(res_two.getUsers().get(29).getId(), 60);

        MvcResult page_three = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "3")
                        .param("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_three = fromJson(objectMapper, page_three.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_three.getUsers().size(), repository.count() - 60);
        assertEquals(res_three.getUsers().get(0).getId(), 61);

        //test for answerer filter
        MvcResult page_ans = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("answerer", "true"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_ans = fromJson(objectMapper, page_ans.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_ans.getUsers().size(), 20);
        for(var res : res_ans.getUsers()){
            assertEquals(res.getPermit(), "a");
        }

        //test for not authenticated
        this.mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden())
                .andReturn();

        //delete testUsers
        for(int i = 3; i <= 62; i++){
            repository.deleteById((long) i);
        }
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
        assertEquals(response.getGender(), user.getGender(),"性别不正确");
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
        assertEquals(user.getGender(),"male");
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