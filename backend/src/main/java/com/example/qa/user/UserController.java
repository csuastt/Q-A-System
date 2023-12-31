package com.example.qa.user;

import com.example.qa.admin.AdminService;
import com.example.qa.admin.model.Admin;
import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.exchange.ChangePasswordRequest;
import com.example.qa.exchange.EarningsResponse;
import com.example.qa.exchange.MonthlyEarnings;
import com.example.qa.exchange.ValueRequest;
import com.example.qa.notification.NotificationService;
import com.example.qa.notification.model.Notification;
import com.example.qa.order.exchange.AcceptRequest;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.User;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.example.qa.security.RestControllerAuthUtils.*;
import static com.example.qa.utils.ReflectionUtils.hasField;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;
    private final NotificationService notificationService;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, AdminService adminService, NotificationService notificationService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
        this.notificationService = notificationService;
    }

    @PostMapping
    public void createUser(@RequestBody RegisterRequest registerRequest) {
        registerRequest.validateOrThrow();
        if (userService.existsByUsername(registerRequest.getUsername())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "USERNAME_INVALID");
        }
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userService.save(new User(registerRequest));
    }

    @GetMapping
    public UserListResponse listUsers(
            @RequestParam(required = false) List<User.Role> role,
            @RequestParam(required = false) Boolean applying,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Sort.Direction sortDirection,
            @RequestParam(required = false) String sortProperty
    ) {
        boolean isAdmin = authLogin() && authIsAdmin();
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.USER_LIST_MAX_PAGE_SIZE);
        if (isAdmin && Boolean.TRUE.equals(applying)) {
            return new UserListResponse(userService.listByApplying(PageRequest.of(page - 1, pageSize)), 2);
        }
        if (!isAdmin) {
            role = List.of(User.Role.ANSWERER);
        }
        if (!hasField(User.class, sortProperty)) {
            sortProperty = "id";
        }
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize,
                Objects.requireNonNullElse(sortDirection, Sort.Direction.ASC), sortProperty);
        Page<User> result = userService.listByRole(role, pageRequest);
        return new UserListResponse(result, isAdmin ? 2 : 0);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable(value = "id") long id) {
        boolean login = authLogin();
        boolean isAdmin = login && authIsAdmin();
        User user = getUserOrThrow(id);
        int userResponseLevel = 0;
        if (login && authIsUser(id)) {
            userResponseLevel = 1;
        } else if (isAdmin) {
            userResponseLevel = 2;
        }
        return new UserResponse(user, userResponseLevel);
    }

    @PutMapping("/{id}")
    public void editUser(@PathVariable(value = "id") long id, @RequestBody UserRequest userRequest) {
        authLoginOrThrow();
        authUserOrSuperAdminOrThrow(id);
        boolean isAdmin = authIsSuperAdmin();
        User user = getUserOrThrow(id);
        if (user.getRole() != User.Role.ANSWERER) {
            userRequest.setPrice(null);
        }
        userRequest.validateOrThrow();
        user.update(userRequest, isAdmin);
        userService.save(user);
    }

    @GetMapping(value = "/{id}/avatar", produces = MediaType.IMAGE_PNG_VALUE)
    public Resource downloadImage(@PathVariable(value = "id") Long id) {
        byte[] image = userService.getById(id).getAvatar();
        if (image == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "No Avatar Found");
        return new ByteArrayResource(image);
    }

    @PostMapping(value = "/{id}/avatar")
    public void uploadImage(@PathVariable(value = "id") Long id, @RequestParam MultipartFile multipartFile) {
        authLoginOrThrow();
        authUserOrThrow(id);
        User user = getUserOrThrow(id);
        try {
            user.setAvatar(multipartFile.getBytes());
            userService.save(user);
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Upload File Not Valid");
        }
    }

    @PutMapping("/{id}/password")
    public void changePassword(@PathVariable(value = "id") long id,
                               @RequestBody ChangePasswordRequest changePasswordRequest) {
        authLoginOrThrow();
        authUserOrSuperAdminOrThrow(id);
        changePasswordRequest.validatePasswordOrThrow();
        User user = getUserOrThrow(id);
        if (!authIsSuperAdmin() && !passwordEncoder.matches(changePasswordRequest.getOriginal(), user.getPassword())) {
            throw new ApiException(403, "WRONG_PASSWORD");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
        userService.save(user);
    }

    @PostMapping("/{id}/apply")
    public void apply(@PathVariable(value = "id") long id,
                      @RequestBody ApplyRequest applyRequest) {
        authLoginOrThrow();
        authUserOrThrow(id);
        User user = getUserOrThrow(id);
        if (user.getRole() == User.Role.ANSWERER || user.isApplying()) {
            throw new ApiException(403, "ALREADY_ANSWERER");
        }
        applyRequest.validateOrThrow();
        user.update(applyRequest);
        user.setApplying(true);
        userService.save(user);
    }

    @PostMapping("/{id}/review")
    public void review(@PathVariable(value = "id") long id, @RequestBody AcceptRequest acceptRequest) {
        authLoginOrThrow();
        authAdminOrThrow();
        if (adminService.getById(authGetId()).getRole() == Admin.Role.ADMIN) {
            throw new ApiException(403, ApiException.NO_PERMISSION);
        }
        User user = getUserOrThrow(id);
        if (!user.isApplying()) {
            throw new ApiException(403);
        }
        user.setApplying(false);
        if (acceptRequest.isAccept()) {
            user.setRole(User.Role.ANSWERER);
        }
        user = userService.save(user);
        notificationService.send(Notification.ofPlain(user, null,
                acceptRequest.isAccept() ? Notification.Type.ANSWERER_APPLICATION_PASSED : Notification.Type.ANSWERER_APPLICATION_REJECTED));
    }

    @PostMapping("/{id}/recharge")
    public void recharge(@PathVariable(value = "id") long id,
                         @RequestBody ValueRequest valueRequest) {
        authLoginOrThrow();
        authUserOrThrow(id);
        User user = getUserOrThrow(id);
        valueRequest.checkRechargeOrThrow(user.getBalance());
        user.setBalance(user.getBalance() + valueRequest.getValue());
        userService.save(user);
    }

    @GetMapping("/{id}/earnings")
    public EarningsResponse getEarnings(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authUserOrAdminOrThrow(id);
        User user = getUserOrThrow(id);
        return new EarningsResponse(user.getEarningsTotal(), MonthlyEarnings.toList(user.getEarningsMonthly()));
    }

    @GetMapping("/{id}/stats")
    public UserStatsResponse getStats(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authUserOrAdminOrThrow(id);
        User user = getUserOrThrow(id);
        return new UserStatsResponse(user);
    }

    private User getUserOrThrow(long id) {
        try {
            return userService.getById(id);
        } catch (UsernameNotFoundException e) {
            throw new ApiException(404);
        }
    }
}
