package com.example.qa.test;

import com.example.qa.admin.AdminRepository;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.storage.StorageProperties;
import com.example.qa.security.SecurityConstants;
import com.example.qa.user.UserRepository;
import com.example.qa.user.exchange.RegisterRequest;
import com.example.qa.user.model.User;
import com.example.qa.utils.MockUtils;
import com.talanlabs.avatargenerator.Avatar;
import com.talanlabs.avatargenerator.IdenticonAvatar;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableConfigurationProperties(StorageProperties.class)
class UpLoadControllerTest {

    private static final String password = "password";
    private static final String question = "TestQuestion";
    private static final String email = "example@example.com";
    private static final String description = "Test Description";
    private static MockUtils mockUtils;
    private static long askerId;
    private static long answererId;

    private static String askerToken;
    private static String answererToken;
    private static String superAdminToken;
    private static String adminToken;
    private static String askerToken2;
    private static String answererToken2;


    @BeforeAll
    static void addUsers(@Autowired MockMvc mockMvc,
                         @Autowired UserRepository userRepository,
                         @Autowired AdminRepository adminRepository,
                         @Autowired PasswordEncoder passwordEncoder) throws Exception {
        mockUtils = new MockUtils(mockMvc);

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername("testAsker10");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        User asker = new User(registerRequest);
        userRepository.save(asker);
        askerId = asker.getId();

        registerRequest.setUsername("testAnswerer10");
        User answerer = new User(registerRequest);
        answerer.setRole(User.Role.ANSWERER);
        userRepository.save(answerer);
        answererId = answerer.getId();

        registerRequest.setUsername("testAsker12");
        registerRequest.setPassword(passwordEncoder.encode(password));
        registerRequest.setEmail(email);
        User asker2 = new User(registerRequest);
        userRepository.save(asker2);
//        askerId = asker2.getId();

        registerRequest.setUsername("testAnswerer12");
        User answerer2 = new User(registerRequest);
        answerer2.setRole(User.Role.ANSWERER);
        userRepository.save(answerer2);
//        answererId = answerer2.getId();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUsername("testAdmin");
        adminRequest.setPassword(passwordEncoder.encode(password));
        adminRequest.setRole(Admin.Role.ADMIN);
        Admin admin = new Admin(adminRequest);
        adminRepository.save(admin);

        LoginRequest userRequest = new LoginRequest();
        userRequest.setUsername("testAsker10");
        userRequest.setPassword(password);
        askerToken = mockUtils.postAndDeserialize("/api/user/login", askerToken, userRequest, status().isOk(), TokenResponse.class).getToken();

        userRequest.setUsername("testAnswerer10");
        userRequest.setPassword(password);
        answererToken = mockUtils.postAndDeserialize("/api/user/login", askerToken, userRequest, status().isOk(), TokenResponse.class).getToken();

        LoginRequest user2Request = new LoginRequest();
        user2Request.setUsername("testAsker12");
        user2Request.setPassword(password);
        askerToken2 = mockUtils.postAndDeserialize("/api/user/login", askerToken, user2Request, status().isOk(), TokenResponse.class).getToken();

        user2Request.setUsername("testAnswerer12");
        user2Request.setPassword(password);
        answererToken2 = mockUtils.postAndDeserialize("/api/user/login", askerToken, user2Request, status().isOk(), TokenResponse.class).getToken();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(SecurityConstants.SUPER_ADMIN_USERNAME);
        loginRequest.setPassword(SecurityConstants.SUPER_ADMIN_PASSWORD);
        TokenResponse tokenResponse = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        superAdminToken = tokenResponse.getToken();

        AdminRequest request = new AdminRequest();
        request.setUsername("testAdmin" + 1);
        request.setRole(Admin.Role.ADMIN);
        request.setPassword("password");
        mockUtils.postUrl("/api/admins", null, request, status().isUnauthorized());
        PasswordResponse passwordResponse = mockUtils.postAndDeserialize("/api/admins", superAdminToken, request, status().isOk(),PasswordResponse.class);
        loginRequest.setUsername("testAdmin" + 1);
        loginRequest.setPassword(passwordResponse.getPassword());
        TokenResponse tokenResponse2 = mockUtils.postAndDeserialize("/api/admin/login", null, loginRequest, status().isOk(), TokenResponse.class);
        adminToken = tokenResponse2.getToken();

    }

    @Test
    long createOrder() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setAsker(askerId);
        request.setAnswerer(answererId);
        request.setTitle(question);
        request.setDescription(description);
        OrderResponse result = mockUtils.postAndDeserialize("/api/orders", askerToken, request, status().isOk(), OrderResponse.class);
        mockUtils.postUrl("/api/orders",answererToken, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",answererToken2, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",askerToken2, request, status().isForbidden());
//        mockUtils.postUrl("/api/orders",superAdminToken, request, status().isOk());
//        mockUtils.postUrl("/api/orders",adminToken, request, status().isOk());

        return result.getId();
    }

    @Test
    void whenFileUploaded_thenVerifyStatus()
            throws Exception {

        long id = createOrder();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );


        Avatar avatar = IdenticonAvatar.newAvatarBuilder().build();
        var img = avatar.create(("wen_ke").hashCode());
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bao);
        MockMultipartFile avatars
                = new MockMultipartFile(
                "img",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                bao.toByteArray()
        );
        mockUtils.multiPart("/api/users/" + askerId + "/avatar",askerToken,avatars,"multipartFile",status().isOk());
        mockUtils.multiPart("/api/users/" + answererId + "/avatar",adminToken,avatars,"multipartFile",status().isForbidden());
        mockUtils.multiPart("/api/users/" + answererId + "/avatar",askerToken,avatars,"multipartFile",status().isForbidden());
        mockUtils.multiPart("/api/users/" + answererId + "/avatar",null,avatars,"multipartFile",status().isUnauthorized());

        mockUtils.multiPart("/api/orders/" + id + "/attachments",askerToken,file,"file",status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",answererToken,file,"file",status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",adminToken,file,"file",status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",answererToken2,file,"file",status().isForbidden());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",askerToken2,file,"file",status().isForbidden());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",null,file,"file",status().isUnauthorized());

        mockUtils.getUrl("/api/orders/" + id + "/attachments",askerToken,null,null,status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/attachments",answererToken,null,null,status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/attachments",adminToken,null,null,status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/attachments",askerToken2,null,null,status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/attachments",answererToken2,null,null,status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/attachments",null,null,null,status().isOk());

        mockUtils.getUrl("/api/users/" + askerId + "/avatar",askerToken,null,null,status().isOk());
        mockUtils.getUrl("/api/users/" + askerId + "/avatar",answererToken,null,null,status().isOk());
        mockUtils.getUrl("/api/users/" + askerId + "/avatar",adminToken,null,null,status().isOk());
        mockUtils.getUrl("/api/users/" + askerId + "/avatar",null,null,null,status().isOk());
    }
}
