package com.example.qa.user;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.manager.model.AppManager;
import com.example.qa.security.UserAuthentication;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.example.qa.user.repository.UserRepository;
import com.example.qa.utils.FieldValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // private final Logger logger = LoggerFactory.getLogger(getClass());

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createUser(@RequestBody RegisterRequest registerRequest) {
        checkUserData(registerRequest);
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "USERNAME_INVALID");
        }
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(new User(registerRequest));
    }

    @GetMapping
    public UserListResponse listUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        if (!authIsAdmin() && role != UserRole.ANSWERER) {
            throw new ApiException(403, "NO_PERMISSION");
        }
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.USER_LIST_MAX_PAGE_SIZE);
        Pageable pageable = Pageable.ofSize(pageSize).withPage(page - 1);
        Page<User> result =
                role == null ? userRepository.findAll(pageable) : userRepository.findAllByRole(role, pageable);
        int userResponseLevel = authIsAdmin() ? 2 : 0;
        return new UserListResponse(result, userResponseLevel);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable(value = "id") long id) {
        // TODO: 非本人或者管理员直接返回 403
        User user = getUserOrThrow(id, authIsAdmin());
        int userResponseLevel = 0;
        if (authIsUser(id)) {
            userResponseLevel = 1;
        } else if (authIsAdmin()) {
            userResponseLevel = 2;
        }
        return new UserResponse(user, userResponseLevel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable(value = "id") long id) {
        if (!authLogin()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED);
        }
        if (!authIsAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NO_PERMISSION");
        }
        User user = getUserOrThrow(id, true);
        if (user.isDeleted()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ALREADY_DELETED");
        }
        user.setDeleted(true);
        user.setUsername(user.getUsername() + "@" + user.getId());
        userRepository.save(user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editUser(@PathVariable(value = "id") long id, @RequestBody UserRequest userRequest) {
        authLoginOrThrow();
        authUserOrAdminOrThrow(id);
        boolean isAdmin = authIsAdmin();
        User user = getUserOrThrow(id, false);
        if (!isAdmin) {
            if (user.getRole() != UserRole.ANSWERER) {
                userRequest.setPrice(null);
            }
            checkUserData(userRequest);
        }
        user.update(userRequest, isAdmin);
        userRepository.save(user);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@PathVariable(value = "id") long id,
                               @RequestBody ChangePasswordRequest changePasswordRequest) {
        authLoginOrThrow();
        authUserOrAdminOrThrow(id);
        validatePassword(changePasswordRequest.getPassword());
        User user = getUserOrThrow(id, false);
        if (!authIsAdmin() && !passwordEncoder.matches(changePasswordRequest.getPassword(), user.getPassword())) {
            throw new ApiException(403, "WRONG_PASSWORD");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
        userRepository.save(user);
    }

    @PostMapping("/{id}/apply")
    @ResponseStatus(HttpStatus.OK)
    public void apply(@PathVariable(value = "id") long id,
                      @RequestBody ApplyRequest applyRequest) {
        authLoginOrThrow();
        authUserOrThrow(id);
        User user = getUserOrThrow(id, false);
        if (user.getRole() == UserRole.ANSWERER) {
            throw new ApiException(403, "ALREADY_ANSWERER");
        }
        checkUserData(applyRequest);
        user.update(applyRequest);
        userRepository.save(user);
    }

    @PostMapping("/{id}/recharge")
    @ResponseStatus(HttpStatus.OK)
    public void recharge(@PathVariable(value = "id") long id,
                         @RequestBody ValueRequest valueRequest) {
        authLoginOrThrow();
        authUserOrThrow(id);
        User user = getUserOrThrow(id, false);
        checkRecharge(user.getBalance(), valueRequest.getValue());
        user.setBalance(user.getBalance() + valueRequest.getValue());
        userRepository.save(user);
    }

    private User getUserOrThrow(long id, boolean allowDeleted) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new ApiException(404);
        }
        User user = userOptional.get();
        if (user.isDeleted() && !allowDeleted) {
            throw new ApiException(404);
        }
        return user;
    }

    private boolean authLogin() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private boolean authIsUser(long id) {
        UserAuthentication auth = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getRole() == User.class && (long) auth.getPrincipal() == id;
    }

    private boolean authIsAdmin() {
        UserAuthentication auth = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getRole() == AppManager.class;
    }

    private void authLoginOrThrow() {
        if (!authLogin()) {
            throw new ApiException(401);
        }
    }

    private void authUserOrThrow(long id) {
        if (!authIsUser(id)) {
            throw new ApiException(403, "NO_PERMISSION");
        }
    }

    private void authUserOrAdminOrThrow(long id) {
        if (!authIsUser(id) && !authIsAdmin()) {
            throw new ApiException(403, "NO_PERMISSION");
        }
    }

    private void validatePassword(String password) {
        if (!FieldValidator.length
                (password, SystemConfig.PASSWORD_MIN_LENGTH, SystemConfig.PASSWORD_MAX_LENGTH)) {
            throw new ApiException(403, "PASSWORD_INVALID");
        }
    }

    private void checkUserData(RegisterRequest request) {
        if (!FieldValidator.length
                (request.getUsername(), SystemConfig.USERNAME_MIN_LENGTH, SystemConfig.USERNAME_MAX_LENGTH)
                || request.getUsername().contains("@")) {
            throw new ApiException(403, "USERNAME_INVALID");
        }
        validatePassword(request.getPassword());
        if (!EmailValidator.getInstance().isValid(request.getEmail())) {
            throw new ApiException(403, "EMAIL_INVALID");
        }
    }

    private void checkUserData(UserRequest request) {
        if (!FieldValidator.lengthIfNotNull
                (request.getNickname(), SystemConfig.NICKNAME_MIN_LENGTH, SystemConfig.NICKNAME_MAX_LENGTH)
        ) {
            throw new ApiException(403, "NICKNAME_INVALID");
        }
        if (!FieldValidator.lengthIfNotNull
                (request.getDescription(), SystemConfig.DESCRIPTION_MIN_LENGTH, SystemConfig.DESCRIPTION_MAX_LENGTH)
        ) {
            throw new ApiException(403, "DESCRIPTION_INVALID");
        }
        if (FieldValidator.valueIfNotNull(request.getPrice(), SystemConfig.PRICE_MIN, SystemConfig.PRICE_MAX)) {
            throw new ApiException(403, "PRICE_INVALID");
        }
    }

    private void checkUserData(ApplyRequest request) {
        if (!FieldValidator.length
                (request.getDescription(), SystemConfig.DESCRIPTION_MIN_LENGTH, SystemConfig.DESCRIPTION_MAX_LENGTH)
        ) {
            throw new ApiException(403, "DESCRIPTION_INVALID");
        }
        if (FieldValidator.value(request.getPrice(), SystemConfig.PRICE_MIN, SystemConfig.PRICE_MAX)) {
            throw new ApiException(403, "PRICE_INVALID");
        }
    }

    private void checkRecharge(int balance, Integer recharge) {
        if (!FieldValidator.value(recharge, 1, SystemConfig.RECHARGE_MAX)) {
            throw new ApiException(403, "RECHARGE_INVALID");
        }
        if (balance + recharge > SystemConfig.BALANCE_MAX) {
            throw new ApiException(403, "BALANCE_INVALID");
        }
    }
}
