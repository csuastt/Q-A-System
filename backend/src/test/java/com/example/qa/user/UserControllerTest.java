package com.example.qa.user;

import com.example.qa.user.exchange.*;
import com.example.qa.user.model.AppUser;
import com.example.qa.user.repository.UserRepository;
import com.example.qa.user.response.*;
import com.google.gson.Gson;
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
    private String token;
    private String password = "password";
    @Autowired
    private UserRepository repository;

    @BeforeAll
    static void addTestUser(
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired UserRepository repository
    ) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new AppUser("testUser", passwordEncoder.encode("password"), authorities);
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
                        .content(new Gson().toJson(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = new Gson().fromJson(loginResult.getResponse().getContentAsString(), LoginResponse.class);
        this.token = response.getToken();
        assertNotNull(response.getToken(), "Token must not be null!");
        assertEquals("testUser", response.getUser().getUsername());

        loginRequest.setPassword("pa");

        //test for wrong password login
        this.mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();
        //test for wrong username
        loginRequest.setUsername("t");
        this.mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(loginRequest)))
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

        GetAllResponse response = new Gson().fromJson(getAllResult.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(20, response.getUsers().size());
        assertEquals(1, response.getUsers().get(0).getId());

        //test for page feature

        MvcResult page_one = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("page", "1")
                        .queryParam("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_one = new Gson().fromJson(page_one.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(30, res_one.getUsers().size());
        assertEquals(1, res_one.getUsers().get(0).getId());

        MvcResult page_two = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("page", "2")
                        .queryParam("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_two = new Gson().fromJson(page_two.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(30, res_two.getUsers().size());
        assertEquals(res_two.getUsers().get(0).getId(), res_one.getUsers().get(29).getId() + 1);
        assertEquals(res_two.getUsers().get(29).getId(), res_one.getUsers().get(29).getId() + 30);

        MvcResult page_three = this.mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("page", "3")
                        .queryParam("limit", "30"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_three = new Gson().fromJson(page_three.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(res_three.getUsers().size(), repository.findAll().size() - res_two.getUsers().get(29).getId() + 2);
        assertEquals(res_three.getUsers().get(0).getId(), res_two.getUsers().get(29).getId() + 1);

        //test for answerer filter
        MvcResult page_ans = this.mockMvc.perform(get("/api/users")
                        .queryParam("answerer", "true"))
                .andExpect(status().isOk())
                .andReturn();

        GetAllResponse res_ans = new Gson().fromJson(page_ans.getResponse().getContentAsString(), GetAllResponse.class);
        assertEquals(20, res_ans.getUsers().size());
        for(var res : res_ans.getUsers()){
            if (repository.findById(res.getId()).isPresent())
                assertEquals("a", repository.findById(res.getId()).get().getPermit());
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
        GetUserResponse response = new Gson().fromJson(getUserResult.getResponse().getContentAsString(), GetUserResponse.class);
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
        assertEquals(response.getAvatarUrl(), user.getAvaUrl(),"头像路径不正确");
        assertEquals(LocalDate.parse(response.getBirthday()), user.getBirthday(),"生日不正确");
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
    void getBasicUser() throws Exception {
        //test for success get user detail
        MvcResult getBasicUserResult = this.mockMvc.perform(get("/api/users/1/basic"))
                .andExpect(status().isOk())
                .andReturn();
        GetBasicUserResponse response = new Gson().fromJson(getBasicUserResult.getResponse().getContentAsString(), GetBasicUserResponse.class);
        assertNotNull(response.getAvatarUrl(),"不能为空");
        assertNotNull(response.getUsername(),"不能为空");
        assertNotNull(response.getId(),"不能为空");
        assertNotNull(response.getNickname(),"不能为空");
        if(repository.findById(1L).isEmpty()){
            throw new Exception("用户不存在");
        }
        var user = repository.findById(1L).get();
        assertEquals(response.getUsername(), user.getUsername(),"用户名不正确");
        assertEquals(response.getAvatarUrl(), user.getAvaUrl(),"头像路径不正确");
        assertEquals(response.getId(), user.getId() ,"id不正确");
        assertEquals(response.getNickname(), user.getNickname(), "昵称不正确");

        long id = repository.count() + 1;

        //test for not existed user
        this.mockMvc.perform(get("/api/users/" + id + "/basic")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void permitQuest() throws Exception{


        //test for success get user permission
        MvcResult getPermissionResult = this.mockMvc.perform(get("/api/users/1/permission")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        if(repository.findById(1L).isEmpty()){
            throw new Exception("用户不存在");
        }
        var user = repository.findById(1L).get();

        PermitAttribute response = new Gson().fromJson(getPermissionResult.getResponse().getContentAsString(), PermitAttribute.class);
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

       RegisterRequest register = new RegisterRequest();
        register.setUsername("eeee");
        register.setPassword("eeee");

        //test register success
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(register)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expected, repository.count());

        //test register existed username
        this.mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(register)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void modifyUser() throws Exception{
        UserAttribute modify = new UserAttribute();
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
                        .content(new Gson().toJson(modify)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //test modify without permission
        this.mockMvc.perform(put("/api/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modify)))
                .andExpect(status().isOk())
                .andReturn();

        if(repository.findById(1L).isEmpty()){
            throw new Exception("用户不存在");
        }
        var user = repository.findById(1L).get();

        assertEquals("eeeee", user.getUsername());
        assertEquals(user.getBirthday(), LocalDate.parse("2000-10-03"));
        assertEquals("@mails.tsinghua.edu.cn", user.getEmail());
        assertEquals("A student", user.getDescription());
        assertEquals("male", user.getGender());
        assertEquals("1010", user.getPhone());
        assertEquals("little", user.getNickname());

        //test modify user not existed
        long id = repository.count() + 1;
        this.mockMvc.perform(put("/api/users/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modify)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //test not authenticated
        this.mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //re-modify
        modify.setUsername("testUser");
        this.mockMvc.perform(put("/api/users/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modify)))
                .andExpect(status().isOk())
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
                        .content(new Gson().toJson(modify)))
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
                        .content(new Gson().toJson(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //test not authenticated
        modify.setOrigin("pass");
        modify.setPassword("password");
        this.mockMvc.perform(put("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modify)))
                .andExpect(status().isForbidden())
                .andReturn();

        //change back to origin
        this.mockMvc.perform(put("/api/users/1/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modify)))
                .andExpect(status().isOk())
                .andReturn();
        this.password = "password";
    }

    @Test
    void getPrice() throws Exception {
        // initial testUser
        generateAnswerer("testAnswerer_g");
        generateUser("testQuestioner_g");
        Long id_ans = 1L;
        Long id_usr = 1L;
        if (repository.findByUsernameAndEnable("testAnswerer_g", true).isPresent())
            id_ans = repository.findByUsernameAndEnable("testAnswerer_g", true)
                    .get().getId();
        if (repository.findByUsernameAndEnable("testQuestioner_g", true).isPresent())
            id_usr = repository.findByUsernameAndEnable("testQuestioner_g", true)
                    .get().getId();

        //test for success get
        MvcResult getPriceResult = this.mockMvc.perform(get("/api/users/" + id_ans + "/price")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        GetPriceResponse response = new Gson().fromJson(getPriceResult.getResponse().getContentAsString(), GetPriceResponse.class);
        if(repository.findById(id_ans).isPresent())
            assertEquals(response.getPrice(), repository.findById(id_ans).get().getPrice());

        //test for questioner get
        this.mockMvc.perform(get("/api/users/" + id_usr + "/price")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andReturn();

        //test for not existed user get
        this.mockMvc.perform(get("/api/users/" + (repository.count() + 1) + "/price")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();

        //delete testUser
        repository.deleteById(id_ans);
        repository.deleteById(id_usr);
    }

    @Test
    void modifyPrice() throws Exception {
        // initial testUser
        generateAnswerer("testAnswerer");
        generateUser("testQuestioner");
        Long id_ans = 1L;
        Long id_usr = 1L;
        if(repository.findByUsernameAndEnable("testAnswerer", true).isPresent())
            id_ans = repository.findByUsernameAndEnable("testAnswerer", true)
                    .get().getId();
        if(repository.findByUsernameAndEnable("testQuestioner", true).isPresent())
            id_usr = repository.findByUsernameAndEnable("testQuestioner", true)
                    .get().getId();

        //test for success modification
        int modifiedPrice = 60;
        PriceAttribute modifyPrice = new PriceAttribute(modifiedPrice);
        this.mockMvc.perform(put("/api/users/" + id_ans + "/price")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modifyPrice)))
                .andExpect(status().isOk())
                .andReturn();
        if(repository.findById(id_ans).isPresent())
            assertEquals(modifiedPrice, repository.findById(id_ans).get().getPrice());

        //test for questioner modification
        this.mockMvc.perform(put("/api/users/" + id_usr + "/price")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modifyPrice)))
                .andExpect(status().isForbidden())
                .andReturn();

        //test for not existed user modification
        this.mockMvc.perform(put("/api/users/" + (repository.count() + 1) + "/price")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modifyPrice)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void modifyPermission() throws Exception {
        PermitAttribute modifyPermission = new PermitAttribute("q");

        //test for successful modification
        this.mockMvc.perform(put("/api/users/1/permission")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modifyPermission)))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("q", repository.findById(1L).get().getPermit());

        //test for useless modification
        this.mockMvc.perform(put("/api/users/1/permission")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modifyPermission)))
                .andExpect(status().isInternalServerError())
                .andReturn();
        assertEquals("q", repository.findById(1L).get().getPermit());

        //test for not existed user modification
        this.mockMvc.perform(put("/api/users/"+(repository.count() + 1) + "/permission")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(modifyPermission)))
                .andExpect(status().isBadRequest())
                .andReturn();
        if (repository.findById(1L).isPresent())
            assertEquals("q", repository.findById(1L).get().getPermit());
    }

    /**
     * generate testUsers with permission a
     * @param username name of answerers
     */
    void generateAnswerer(String username){
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        var user = new AppUser(username, passwordEncoder.encode("password"),authorities);
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
        var user = new AppUser(username, passwordEncoder.encode("password"),authorities);
        repository.save(user);
    }
}