package com.example.qa.user;

import com.example.qa.user.exchange.*;
import com.example.qa.user.model.AppUser;
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
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger("UserControl");
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @permission          Authenticated
     * @param is_answerer   Whether answerers
     * @param page          Page Number
     * @param limit         Item Number
     * @return              A List of AppUser requested
     */
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

    /**
     * @permission          Authenticated
     * @param id            Unique to specify a user
     *@return               Basic information for required user
     */
    @GetMapping("/api/users/{id}/basic")
    public BasicUserData getBasicUser(@PathVariable(value = "id") Long id){
        Optional<AppUser> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        return new BasicUserData(optionalUser.get());
    }

    /**
     * @permission Authentication
     * @param id   Unique to specify a user
     * @return     Detailed information for required user
     */
    @GetMapping("/api/users/{id}")
    public UserData getUser(@PathVariable(value = "id") Long id) {
        Optional<AppUser> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        return new UserData(optionalUser.get());
    }

    /**
     * @permission Authentication
     * @param id   Unique to specify a user
     * @return     Permission of the required user
     */
    @GetMapping("/api/users/{id}/permission")
    public PermitAttribute permitQuest(@PathVariable(value = "id") Long id){
        Optional<AppUser> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        String permit = optionalUser.get().getPermit();
        return new PermitAttribute(permit);
    }

    /**
     * @permission Authentication
     * @param id   Unique to specify a user
     * @return     Success or Not Found
     */
    @DeleteMapping("/api/users")
    public SuccessResponse deleteUser(@RequestParam(value = "id") Long id) {
        if(userRepository.existsByIdAndEnable(id, true)){
            var user = userRepository.findById(id).get();
            user.setEnable(false);
            try{
                userRepository.save(user);
                return new SuccessResponse("删除成功");
            }catch (Exception e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "数据库出错");
            }

        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户不存在");
        }
    }

    /**
     * @permission Authenticated
     * @param id   Unique to specify a user
     * @return     A generated avatar for the specific user
     */
    @GetMapping(value = "/api/users/avatar/{id}.png", produces = "image/png")
    public @ResponseBody byte[] genAvatar(@PathVariable(value = "id") Long id)  {
        try {
            Avatar avatar = IdenticonAvatar.newAvatarBuilder().build();
            var img = avatar.create((id + "Q & A backend").hashCode());
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bao);
            return bao.toByteArray();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @permission            Public
     * @param registeredUser  Body to register
     * @return                Success or not
     */
    @PostMapping("/api/users")
    public SuccessResponse register( @RequestBody UserAttribute registeredUser) {
        if (userRepository.existsByUsernameAndEnable(registeredUser.username, true))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        checkValidation(registeredUser);
        AppUser userInfo = new AppUser(registeredUser);
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userInfo.setAuthorities(authorities);
        userRepository.save(userInfo);
        userInfo.setAva();
        userRepository.save(userInfo);
        return new SuccessResponse("注册成功");
    }

    private void checkValidation(@RequestBody UserAttribute registeredUser) {
        if(registeredUser.getUsername() == null || registeredUser.getUsername().length() < 4)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名长度小于");
        if(registeredUser.getNickname() != null && registeredUser.getNickname().length() < 4)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "昵称长度小于4");
        if(registeredUser.getGender() != null && (!registeredUser.getGender().equals("male") && !registeredUser.getGender().equals("female")))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "性别错误");
    }

    /**
     * @permission          Authenticated
     * @param id            Unique to specify a user
     * @param modifiedUser  Body for modification
     * @return              Success or not
     */
    @PutMapping("/api/users/{id}")
    public SuccessResponse modifyUser(@PathVariable(value = "id") Long id,
                                      @RequestBody UserAttribute modifiedUser){
        Optional<AppUser> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long cu_id = Long.parseLong((String) auth.getPrincipal());
        if(!id.equals(cu_id)){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "没有修改权限");
        }
        checkValidation(modifiedUser);
        optionalUser.get().updateUserInfo(modifiedUser);
        userRepository.save(optionalUser.get());
        return new SuccessResponse("修改成功");
    }

    /**
     * @permission          Authenticated
     * @param id            Unique to specify a user
     * @param modifiedUser  Body for password modification
     * @return              Success or Origin not match
     */
    @PutMapping("/api/users/{id}/password")
    public SuccessResponse modifyPass(@PathVariable(value = "id") Long id,
                                      @RequestBody ModifyPasswordAttribute modifiedUser) {
        Optional<AppUser> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        if(passwordEncoder.matches(modifiedUser.getOrigin(),optionalUser.get().getPassword())){
            optionalUser.get().setPassword(passwordEncoder.encode(modifiedUser.getPassword()));
            userRepository.save(optionalUser.get());
            return new SuccessResponse("修改密码成功");
        }

        userRepository.save(optionalUser.get());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "原密码不正确");
    }

    /**
     * @permission    Authenticated and be an answerer
     * @param id      Unique to specify a user
     * @return        Price of the required user
     */
    @GetMapping("/api/users/{id}/price")
    public PriceAttribute getPrice(@PathVariable(value = "id") Long id){
        Optional<AppUser> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        var user = optionalUser.get();
        if(user.getPermit().equals("q"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return new PriceAttribute(user.getPrice());
    }

    /**
     * @permission          Authenticated
     * @param id            Unique to specify a user
     * @param modifyPrice   Price to pass
     * @return              Success or not authenticated
     */
    @PutMapping("/api/users/{id}/price")
    public SuccessResponse modifyPrice(@PathVariable(value = "id") Long id,
                                       @RequestBody PriceAttribute modifyPrice){
        Optional<AppUser> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        var user = optionalUser.get();
        if(user.getPermit().equals("q"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        user.setPrice(modifyPrice.getPrice());
        userRepository.save(user);
        return new SuccessResponse("修改价格成功");
    }

    /**
     * @permission          Authenticated
     * @param id            Unique to specify a user
     * @param modifyPermit  Permit to be modified
     * @return              Success or useless
     */
    @PutMapping("/api/users/{id}/permission")
    public SuccessResponse modifyPermission(@PathVariable(value = "id") Long id,
                                            @RequestBody PermitAttribute modifyPermit){
        Optional<AppUser> optionalUser = userRepository.findById(id);
        checkActivity(optionalUser);
        var user = optionalUser.get();
        String permit = modifyPermit.getPermit();
        if(user.getPermit().equals(permit))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "无效修改");
        user.setPermit(permit);
        userRepository.save(user);
        return new SuccessResponse("修改用户权限成功");
    }

    /**
     * Check if it is a soft_deleted user or not existed
     * @param optionalUser   The user to test
     */
    private void checkActivity(Optional<AppUser> optionalUser) {
        if (optionalUser.isEmpty()|| !optionalUser.get().isEnable())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "未找到用户");
    }
}
