package com.example.qa.user;

import com.example.qa.errorhandling.ApiException;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.User;
import com.example.qa.user.repository.UserRepository;
import com.talanlabs.avatargenerator.Avatar;
import com.talanlabs.avatargenerator.IdenticonAvatar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/api/users")
    public GetAllData getUsers(@RequestParam(value = "answerer", defaultValue = "false")Boolean is_answerer,
                               @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "limit", defaultValue = "20")Integer limit)
    {
        var response = new GetAllData();
        if(!is_answerer) {
            for (var user : userRepository.findAll(limit, (page - 1) * limit)) {
                response.getUsers().add(new BasicUserData(user));
            }
        }else{
            for (var user : userRepository.findAllByPermit(limit, (page - 1) * limit, "a")) {
                response.getUsers().add(new BasicUserData(user));
            }
        }
        return response;
    }

    @GetMapping("/api/users/{id}/basic")
    public BasicUserData getBasicUser(@PathVariable(value = "id") Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        return new BasicUserData(optionalUser.get());
    }

    @GetMapping("/api/users/{id}")
    public UserResponse getUser(@PathVariable(value = "id") Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        return new UserResponse(optionalUser.get());
    }

    @GetMapping("/api/users/{id}/permission")
    public PermitAttribute permitQuest(@PathVariable(value = "id") Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        String permit = optionalUser.get().getPermit();
        return new PermitAttribute(permit);
    }

    @DeleteMapping("/api/users")
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

    @PostMapping("/api/users")
    public SuccessResponse register( @RequestBody UserRequest registeredUser) {
        if (userRepository.existsByUsernameAndEnable(registeredUser.username, true))
            throw new ApiException(HttpStatus.FORBIDDEN);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        checkValidation(registeredUser);
        User userInfo = new User(registeredUser);
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userInfo.setAuthorities(authorities);
        userRepository.save(userInfo);
        userInfo.setAva();
        userRepository.save(userInfo);
        return new SuccessResponse("注册成功");
    }

    private void checkValidation(@RequestBody UserRequest registeredUser) {
        if(registeredUser.getUsername() == null || registeredUser.getUsername().length() < 4)
            throw new ApiException(HttpStatus.BAD_REQUEST, "用户名长度小于4");
        if(registeredUser.getNickname() != null && registeredUser.getNickname().length() > 10)
            throw new ApiException(HttpStatus.BAD_REQUEST, "昵称长度大于10");
        if(registeredUser.getGender() != null && (!registeredUser.getGender().equals("male") && !registeredUser.getGender().equals("female") && !registeredUser.getGender().equals("unknown")))
            throw new ApiException(HttpStatus.BAD_REQUEST, "性别错误");
    }

    private void checkValidationModify(@RequestBody UserRequest registeredUser) {
        if(registeredUser.getNickname() != null && registeredUser.getNickname().length() > 10)
            throw new ApiException(HttpStatus.BAD_REQUEST, "昵称长度大于10");
        if(registeredUser.getUsername() != null && registeredUser.getUsername().length() < 4)
            throw new ApiException(HttpStatus.BAD_REQUEST, "用户名长度小于4");
        if(registeredUser.getGender() != null && (!registeredUser.getGender().equals("male") && !registeredUser.getGender().equals("female") && !registeredUser.getGender().equals("unknown")))
            throw new ApiException(HttpStatus.BAD_REQUEST, "性别错误");
    }

    @PutMapping("/api/users/{id}")
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

    @PutMapping("/api/users/{id}/password")
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

    private void checkActivity(Optional<User> optionalUser) {
        if (optionalUser.isEmpty()|| !optionalUser.get().getEnable())
            throw new ApiException(HttpStatus.BAD_REQUEST, "未找到用户");
    }
}
