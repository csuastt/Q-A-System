package com.example.qa.user;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.example.qa.utils.FieldValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.example.qa.security.RestControllerAuthUtils.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createUser(@RequestBody RegisterRequest registerRequest) {
        checkUserData(registerRequest);
        if (userService.existsByUsername(registerRequest.getUsername())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "USERNAME_INVALID");
        }
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userService.save(new User(registerRequest));
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
        Page<User> result = userService.listByRole(role, pageable);
        int userResponseLevel = authIsAdmin() ? 2 : 0;
        return new UserListResponse(result, userResponseLevel);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable(value = "id") long id) {
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
        userService.save(user);
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
        userService.save(user);
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
        userService.save(user);
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
        user.setRole(UserRole.ANSWERER);
        userService.save(user);
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
        userService.save(user);
    }

    private User getUserOrThrow(long id, boolean allowDeleted) {
        try {
            return userService.getById(id, allowDeleted);
        } catch (UsernameNotFoundException e) {
            throw new ApiException(404);
        }
    }

    public void validatePassword(String password) {
        if (!FieldValidator.length
                (password, SystemConfig.PASSWORD_MIN_LENGTH, SystemConfig.PASSWORD_MAX_LENGTH)) {
            throw new ApiException(403, "PASSWORD_INVALID");
        }
    }

    public void checkUserData(RegisterRequest request) {
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

    public void checkUserData(UserRequest request) {
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
        if (!FieldValidator.valueIfNotNull(request.getPrice(), SystemConfig.PRICE_MIN, SystemConfig.PRICE_MAX)) {
            throw new ApiException(403, "PRICE_INVALID");
        }
    }

    public void checkUserData(ApplyRequest request) {
        if (!FieldValidator.length
                (request.getDescription(), SystemConfig.DESCRIPTION_MIN_LENGTH, SystemConfig.DESCRIPTION_MAX_LENGTH)
        ) {
            throw new ApiException(403, "DESCRIPTION_INVALID");
        }
        if (!FieldValidator.value(request.getPrice(), SystemConfig.PRICE_MIN, SystemConfig.PRICE_MAX)) {
            throw new ApiException(403, "PRICE_INVALID");
        }
    }

    public void checkRecharge(int balance, Integer recharge) {
        if (!FieldValidator.value(recharge, 1, SystemConfig.RECHARGE_MAX)) {
            throw new ApiException(403, "RECHARGE_INVALID");
        }
        if (balance + recharge > SystemConfig.BALANCE_MAX) {
            throw new ApiException(403, "BALANCE_INVALID");
        }
    }
}
