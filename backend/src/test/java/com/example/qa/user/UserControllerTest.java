package com.example.qa.user;

import com.example.qa.user.exchange.*;
import com.example.qa.user.model.User;
import com.example.qa.user.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeAll;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;

    private String token;
    private final String password = "password";
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeAll
    static void addTestUser(
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired UserRepository repository
    ) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new User("testUser", passwordEncoder.encode("password"), authorities);
        user.setPermit("a");
        repository.save(user);
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
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class);
        this.token = response.getToken();
        assertNotNull(response.getToken(), "Token must not be null!");
        assertEquals(response.getUser().getUsername(), "testUser");

        loginRequest.setPassword("pa");

        //test for wrong password login
        this.mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();
        //test for wrong username
        loginRequest.setUsername("t");
        this.mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void getUsers() throws Exception {

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

        GetAllResponse response = objectMapper.readValue(getAllResult.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(response.getUsers().size(), 20);
        assertEquals(response.getUsers().get(0).getId(), 1);

        //test for page feature

        MvcResult page_one = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("page", "1")
                        .queryParam("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_one = objectMapper.readValue(page_one.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_one.getUsers().size(), 30);
        assertEquals(res_one.getUsers().get(0).getId(), 1);

        MvcResult page_two = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("page", "2")
                        .queryParam("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_two = objectMapper.readValue(page_two.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_two.getUsers().size(), 30);
        assertEquals(res_two.getUsers().get(0).getId(), res_one.getUsers().get(29).getId() + 1);
        assertEquals(res_two.getUsers().get(29).getId(), res_one.getUsers().get(29).getId() + 30);

        MvcResult page_three = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("page", "3")
                        .queryParam("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_three = objectMapper.readValue(page_three.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_three.getUsers().size(), repository.findAll().size() - res_two.getUsers().get(29).getId() + 2);
        assertEquals(res_three.getUsers().get(0).getId(), res_two.getUsers().get(29).getId() + 1);

        //test for answerer filter
        MvcResult page_ans = this.mockMvc.perform(get("/api/users")
                        .queryParam("answerer", "true"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_ans = objectMapper.readValue(page_ans.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_ans.getUsers().size(), 20);
        for(var res : res_ans.getUsers()){
            assertEquals(repository.findById(res.getId()).get().getPermit(), "a");
        }

        //test for not authenticated
        this.mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden())
                .andReturn();

        //delete testUsers
        for(int i = 7; i <= 66; i++){
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
        GetUserResponse response = objectMapper.readValue(getUserResult.getResponse().getContentAsString(), GetUserResponse.class);
        assertNotNull(response.getAvatarUrl(),"不能为空");
        assertNotNull(response.getBirthday(),"不能为空");
        assertNotNull(response.getUsername(),"不能为空");
        assertNotNull(response.getGender(),"不能为空");
        assertNotNull(response.getId(),"不能为空");
        assertNotNull(response.getEmail(),"不能为空");
        assertNotNull(response.getNickname(),"不能为空");
        assertNotNull(response.getCreateTime(),"不能为空");
        if(repository.findById(1L).isEmpty()){
            throw new Exception("用户不存在");
        }
        var user = repository.findById(1L).get();
        assertEquals(response.getUsername(), user.getUsername(),"用户名不正确");
        assertEquals(response.getGender(), user.getGender(),"性别不正确");
        assertEquals(response.getId(), user.getId() ,"id不正确");
        assertEquals(response.getEmail(), user.getEmail(), "邮件不正确");
        assertEquals(response.getNickname(), user.getNickname(), "昵称不正确");

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
    void deleteUser() throws Exception{

        //test for success delete user
        long expected = repository.count() - 1;
        this.mockMvc.perform(delete("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("id", "2"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(expected, repository.findAllByEnable(true).size());

        //test for user deleted delete
        this.mockMvc.perform(delete("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("id", "2"))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(expected, repository.findAllByEnable(true).size());

        //test for user not existed delete
        this.mockMvc.perform(delete("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .param("id", "10"))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertEquals(expected, repository.findAllByEnable(true).size());

        //test for not authenticated
        this.mockMvc.perform(delete("/api/users")
                        .param("id", "1"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void register() throws Exception{

        long expected = repository.count() + 1;

       RegisterRequest register = new RegisterRequest();
        register.setUsername("eeee");
        register.setPassword("eeee");

        //test register success
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expected, repository.count());

        //test register existed username
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void modifyUser() throws Exception{
        UserRequest modify = new UserRequest();
        modify.setUsername("eeeee");
        modify.setPassword("eeeee");
        modify.setBirthday("2000-10-03");
        modify.setDescription("A student");
        modify.setEmail("@mails.tsinghua.edu.cn");
        modify.setGender("male");
        modify.setPhone("1010");
        modify.setNickname("little");

        //test modify without permission
        this.mockMvc.perform(put("/api/users/2")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //test modify without permission
        this.mockMvc.perform(put("/api/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isOk())
                .andReturn();

        if(repository.findById(1L).isEmpty()){
            throw new Exception("用户不存在");
        }
        var user = repository.findById(1L).get();

        assertEquals(user.getUsername(), "eeeee");
        assertEquals(user.getBirthday(), LocalDate.parse("2000-10-03"));
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
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //test not authenticated
        this.mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //re-modify
        modify.setUsername("testUser");
        this.mockMvc.perform(put("/api/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void modifyPass() throws Exception{
        ChangePasswordRequest modify = new ChangePasswordRequest();
        modify.setOrigin("password");
        modify.setPassword("pass");

        //test success modify password
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modify)))
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
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //test not authenticated
        modify.setOrigin("pass");
        modify.setPassword("password");
        this.mockMvc.perform(put("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //change back to origin
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isOk())
                .andReturn();
        this.password = "password";
    }

    /**
     * generate testUsers with permission a
     * @param username name of answerers
     */
    void generateAnswerer(String username){
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new User(username, passwordEncoder.encode("password"),authorities);
        user.setPermit("a");
        user.setPrice(30);
        repository.save(user);
    }

    /**
     * generate testUsers
     * @param username name to create
     */
    void generateUser(String username){
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new User(username, passwordEncoder.encode("password"),authorities);
        repository.save(user);
    }
}