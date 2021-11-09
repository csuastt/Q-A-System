package com.example.qa.user;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.exchange.ChangePasswordRequest;
import com.example.qa.exchange.EarningsResponse;
import com.example.qa.exchange.MonthlyEarnings;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
            @RequestParam(required = false) List<UserRole> role,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        boolean isAdmin = authLogin() && authIsAdmin();
        if (!isAdmin) {
            role = List.of(UserRole.ANSWERER);
        }
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.USER_LIST_MAX_PAGE_SIZE);
        PageRequest pageRequest = PageRequest.ofSize(pageSize).withPage(page - 1)
                .withSort(Sort.Direction.ASC, "id");
        Page<User> result = userService.listByRole(role, pageRequest);
        int userResponseLevel = isAdmin ? 2 : 0;
        return new UserListResponse(result, userResponseLevel);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable(value = "id") long id) {
        boolean login = authLogin();
        boolean isAdmin = login && authIsAdmin();
        User user = getUserOrThrow(id, isAdmin);
        int userResponseLevel = 0;
        if (login && authIsUser(id)) {
            userResponseLevel = 1;
        } else if (isAdmin) {
            userResponseLevel = 2;
        }
        return new UserResponse(user, userResponseLevel);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        User user = getUserOrThrow(id, true);
        if (user.isDeleted()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "ALREADY_DELETED");
        }
        user.setDeleted(true);
        user.setUsername(user.getUsername() + "@" + user.getId());
        userService.save(user);
    }

    @PutMapping("/{id}")
    public void editUser(@PathVariable(value = "id") long id, @RequestBody UserRequest userRequest) {
        authLoginOrThrow();
        authUserOrSuperAdminOrThrow(id);
        boolean isAdmin = authIsSuperAdmin();
        User user = getUserOrThrow(id, false);
        if (user.getRole() != UserRole.ANSWERER) {
            userRequest.setPrice(null);
        }
        userRequest.validateOrThrow();
        user.update(userRequest, isAdmin);
        userService.save(user);
    }

    @PutMapping("/{id}/password")
    public void changePassword(@PathVariable(value = "id") long id,
                               @RequestBody ChangePasswordRequest changePasswordRequest) {
        authLoginOrThrow();
        authUserOrSuperAdminOrThrow(id);
        changePasswordRequest.validatePasswordOrThrow();
        User user = getUserOrThrow(id, false);
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
        User user = getUserOrThrow(id, false);
        if (user.getRole() == UserRole.ANSWERER) {
            throw new ApiException(403, "ALREADY_ANSWERER");
        }
        applyRequest.validateOrThrow();
        user.update(applyRequest);
        user.setRole(UserRole.ANSWERER);
        userService.save(user);
    }

//    @PostMapping("/{id}/avatar")
//    public String upLoad(@RequestParam(value = "file") MultipartFile file, Model model, HttpServletRequest request){
//        if (file.isEmpty()) {
//            System.out.println("文件为空空");
//        }
//        String fileName = file.getOriginalFilename();  // 文件名
//        String suffixName = fileName.substring(fileName.lastIndexOf("."));  // 后缀名
//        String filePath = "D://temp-rainy//"; // 上传后的路径
//        fileName = UUID.randomUUID() + suffixName; // 新文件名
//        File dest = new File(filePath + fileName);
//        if (!dest.getParentFile().exists()) {
//            dest.getParentFile().mkdirs();
//        }
//        try {
//            file.transferTo(dest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String filename = "/temp-rainy/" + fileName;
//        model.addAttribute("filename", filename);
//        return "file";
//    }




    @PostMapping("/{id}/recharge")
    public void recharge(@PathVariable(value = "id") long id,
                         @RequestBody ValueRequest valueRequest) {
        authLoginOrThrow();
        authUserOrThrow(id);
        User user = getUserOrThrow(id, false);
        valueRequest.checkRechargeOrThrow(user.getBalance());
        user.setBalance(user.getBalance() + valueRequest.getValue());
        userService.save(user);
    }

    @GetMapping("/{id}/earnings")
    public EarningsResponse getEarnings(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authUserOrAdminOrThrow(id);
        User user = getUserOrThrow(id, false);
        return new EarningsResponse(user.getEarningsTotal(), MonthlyEarnings.toList(user.getEarningsMonthly()));
    }

    @GetMapping("/{id}/stats")
    public UserStatsResponse getStats(@PathVariable(value = "id") long id) {
        authLoginOrThrow();
        authUserOrAdminOrThrow(id);
        User user = getUserOrThrow(id, false);
        return new UserStatsResponse(user);
    }

    private User getUserOrThrow(long id, boolean allowDeleted) {
        try {
            return userService.getById(id, allowDeleted);
        } catch (UsernameNotFoundException e) {
            throw new ApiException(404);
        }
    }
}
