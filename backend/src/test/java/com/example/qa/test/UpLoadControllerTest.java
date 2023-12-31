package com.example.qa.test;

import com.example.qa.admin.AdminRepository;
import com.example.qa.admin.exchange.AdminRequest;
import com.example.qa.admin.exchange.PasswordResponse;
import com.example.qa.admin.model.Admin;
import com.example.qa.exchange.LoginRequest;
import com.example.qa.exchange.TokenResponse;
import com.example.qa.exchange.ValueRequest;
import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.order.exchange.OrderResponse;
import com.example.qa.order.model.Attachment;
import com.example.qa.order.model.Order;
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
import java.util.UUID;

import static com.example.qa.utils.JsonUtils.mapper;
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

        Attachment attachment = mapper.readValue(mockUtils.multiPart("/api/orders/" + id + "/attachments",askerToken,file,"file",status().isOk()).getResponse().getContentAsString(), Attachment.class);
        mockUtils.getUrl("/api/orders/" + id + "/attachments/" + attachment.getUuid(), answererToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/attachments/" + attachment.getUuid(), answererToken2, null, null, status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/attachments/" + attachment.getUuid(), null, null, null, status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",answererToken,file,"file",status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",adminToken,file,"file",status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",answererToken2,file,"file",status().isForbidden());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",askerToken2,file,"file",status().isForbidden());
        mockUtils.multiPart("/api/orders/" + id + "/attachments",null,file,"file",status().isUnauthorized());

        UUID uuid = mapper.readValue(mockUtils.multiPart("/api/orders/" + id + "/pictures",askerToken,avatars,"pic",status().isOk()).getResponse().getContentAsString(), UUID.class);
        mockUtils.getUrl("/api/orders/" + id + "/pictures/" + uuid, answererToken, null, null, status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/pictures/" + uuid, answererToken2, null, null, status().isOk());
        mockUtils.getUrl("/api/orders/" + id + "/pictures/" + uuid, null, null, null, status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/pictures",answererToken,avatars,"pic",status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/pictures",adminToken,avatars,"pic",status().isOk());
        mockUtils.multiPart("/api/orders/" + id + "/pictures",answererToken2,avatars,"pic",status().isForbidden());
        mockUtils.multiPart("/api/orders/" + id + "/pictures",askerToken2,avatars,"pic",status().isForbidden());
        mockUtils.multiPart("/api/orders/" + id + "/pictures",null,avatars,"pic",status().isUnauthorized());

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
        mockUtils.getUrl("/api/users/" + answererId + "/avatar",answererToken,null,null,status().isNotFound());

        mockUtils.getUrl("/api/users/" + askerId + "/earnings",askerToken,null,null,status().isOk());
        mockUtils.getUrl("/api/users/" + askerId + "/earnings",answererToken,null,null,status().isForbidden());
        mockUtils.getUrl("/api/users/" + askerId + "/earnings",adminToken,null,null,status().isOk());
        mockUtils.getUrl("/api/users/" + askerId + "/earnings",null,null,null,status().isUnauthorized());

        mockUtils.getUrl("/api/users/" + askerId + "/stats",askerToken,null,null,status().isOk());
        mockUtils.getUrl("/api/users/" + askerId + "/stats",answererToken,null,null,status().isForbidden());
        mockUtils.getUrl("/api/users/" + askerId + "/stats",adminToken,null,null,status().isOk());
        mockUtils.getUrl("/api/users/" + askerId + "/stats",null,null,null,status().isUnauthorized());
    }

    @Test
    void testPurchasing() throws Exception {
        long id = createOrder();

        OrderRequest request = new OrderRequest();
        request.setShowPublic(true);
        request.setPublicPrice(2);
        request.setState(Order.State.CHAT_ENDED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/purchase", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken2, null, status().isOk());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken, null, status().isForbidden());
        request.setShowPublic(false);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/purchase", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken2, null, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken, null, status().isForbidden());
        request.setShowPublic(true);
        request.setPublicPrice(-1);
        mockUtils.postUrl("/api/orders/" + id + "/purchase", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken2, null, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken, null, status().isForbidden());
        request.setShowPublic(false);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/purchase", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken2, null, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken, null, status().isForbidden());
        request.setState(Order.State.ACCEPTED);
        mockUtils.postUrl("/api/orders/" + id + "/purchase", null, null, status().isUnauthorized());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken2, null, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/purchase", askerToken, null, status().isForbidden());
    }

    @Test
    void testRating() throws Exception {

        long id = createOrder();

        OrderRequest request = new OrderRequest();
        request.setShowPublic(true);
        request.setPublicPrice(2);
        request.setState(Order.State.CHAT_ENDED);
        edit(id, request);

        ValueRequest request1 = new ValueRequest();
        request1.setValue(6);
        request1.setText("12345");

        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken, request1, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken2, request1, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/rate",answererToken, request1, status().isForbidden());
        request1.setValue(1);
        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken, request1, status().isOk());
        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken, request1, status().isForbidden());

        request1.setText(null);

        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken, request1, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken2, request1, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/rate",answererToken, request1, status().isForbidden());

        request1.setText("01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                         "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                         "01234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                         "01234567890123456789012345678901234567890123456789012345678901234567890123456789");

        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken, request1, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken2, request1, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/rate",answererToken, request1, status().isForbidden());

        request1.setText("1");
        request.setState(Order.State.ACCEPTED);
        edit(id, request);
        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken, request1, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/rate",askerToken2, request1, status().isForbidden());
        mockUtils.postUrl("/api/orders/" + id + "/rate",answererToken, request1, status().isForbidden());
    }

    @Test
    void testConfig() throws Exception {
        mockUtils.getUrl("/api/config/stats", superAdminToken, null, null, status().isOk());
        mockUtils.getUrl("/api/config/stats", askerToken2, null, null, status().isForbidden());
        mockUtils.getUrl("/api/config/stats", null, null, null, status().isUnauthorized());
    }

    void edit(long id, OrderRequest data) throws Exception {
        mockUtils.putUrl("/api/orders/" + id, superAdminToken, data, status().isOk());
    }
}
