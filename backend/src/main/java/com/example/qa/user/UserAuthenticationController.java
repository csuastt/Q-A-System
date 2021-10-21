package com.example.qa.user;

import com.example.qa.errorhandling.ApiException;
import com.example.qa.user.exchange.AuthenticationSuccessResponse;
import com.example.qa.user.exchange.LoginRequest;
import com.example.qa.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.qa.security.JwtUtils.userToken;

@RestController
@RequestMapping("/api/user")
public class UserAuthenticationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public UserAuthenticationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public AuthenticationSuccessResponse login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            throw new ApiException(400);
        }
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()) {
            logger.info("login: username '{}' not found", loginRequest.getUsername());
            throw new ApiException(403);
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.info("login: wrong password for user '{}'", loginRequest.getUsername());
            throw new ApiException(403);
        }
        logger.info("login: login success for user '{}'", loginRequest.getUsername());
        return new AuthenticationSuccessResponse(userToken(user.getId()), user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout() {
        // 目前退出无需任何操作
    }
}
