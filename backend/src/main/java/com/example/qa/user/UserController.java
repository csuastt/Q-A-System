package com.example.qa.user;

import com.example.qa.config.SystemConfig;
import com.example.qa.errorhandling.ApiException;
import com.example.qa.manager.model.AppManager;
import com.example.qa.security.UserAuthentication;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.User;
import com.example.qa.user.model.UserRole;
import com.example.qa.user.repository.UserRepository;
import com.example.qa.utils.StringValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody RegisterRequest registerRequest) {
        checkUserData(registerRequest);
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "USERNAME_INVALID");
        }
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(new User(registerRequest));
    }

    @GetMapping
    public UserListResponse list(
            @RequestParam Optional<UserRole> role,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "1") int page
    ) {
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        pageSize = Math.min(pageSize, SystemConfig.USER_LIST_MAX_PAGE_SIZE);
        Pageable pageable = Pageable.ofSize(pageSize).withPage(page - 1);
        Page<User> result =
                role.isEmpty() ? userRepository.findAll(pageable) : userRepository.findAllByRole(role.get(), pageable);
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        int userResponseLevel = authentication != null && authentication.getRole() == AppManager.class ? 2 : 0;
        return new UserListResponse(result, userResponseLevel);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable(value = "id") Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        return new UserResponse(optionalUser.get());
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deleteUser(@RequestParam(value = "id") Long id) {
        if(userRepository.existsByIdAndEnable(id, true)){
            var user = userRepository.findById(id).get();
            user.setEnable(false);
            try{
                userRepository.save(user);
                return new SuccessResponse("删除成功");
            }catch (Exception e){
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "数据库出错");
            }

        }else{
            throw new ApiException(HttpStatus.BAD_REQUEST, "用户不存在");
        }
    }

    @PutMapping("/{id}")
    public SuccessResponse modifyUser(@PathVariable(value = "id") Long id,
                                      @RequestBody UserRequest modifiedUser){
        Optional<User> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long cu_id = Long.parseLong((String) auth.getPrincipal());
        if(!id.equals(cu_id)){
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "没有修改权限");
        }
        checkValidationModify(modifiedUser);
        optionalUser.get().updateUserInfo(modifiedUser);
        userRepository.save(optionalUser.get());
        return new SuccessResponse("修改成功");
    }

    @PutMapping("/{id}/password")
    public SuccessResponse modifyPass(@PathVariable(value = "id") Long id,
                                      @RequestBody ChangePasswordRequest modifiedUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        if(passwordEncoder.matches(modifiedUser.getOrigin(),optionalUser.get().getPassword())){
            optionalUser.get().setPassword(passwordEncoder.encode(modifiedUser.getPassword()));
            userRepository.save(optionalUser.get());
            return new SuccessResponse("修改密码成功");
        }

        userRepository.save(optionalUser.get());
        throw new ApiException(HttpStatus.FORBIDDEN, "原密码不正确");
    }

    private void checkUserData(RegisterRequest request) {
        if (!StringValidator.length
                (request.getUsername(), SystemConfig.USERNAME_MIN_LENGTH, SystemConfig.USERNAME_MAX_LENGTH)
                || request.getUsername().contains("@")) {
            throw new ApiException(403, "USERNAME_INVALID");
        }
        if (!StringValidator.length
                (request.getPassword(), SystemConfig.PASSWORD_MIN_LENGTH, SystemConfig.PASSWORD_MAX_LENGTH)) {
            throw new ApiException(403, "PASSWORD_INVALID");
        }
        if (!EmailValidator.getInstance().isValid(request.getEmail())) {
            throw new ApiException(403, "EMAIL_INVALID");
        }
    }
}
