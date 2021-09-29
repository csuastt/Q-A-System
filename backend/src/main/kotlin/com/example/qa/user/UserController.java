package com.example.qa.user;

import com.example.qa.user.exchange.*;
import com.example.qa.user.model.AppUser;
import com.talanlabs.avatargenerator.Avatar;
import com.talanlabs.avatargenerator.IdenticonAvatar;
import com.example.qa.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/api/users")
    public Iterable<AppUser> getUsers(){
        return userRepository.findAll();
    }



    @GetMapping("/api/user/{username}")
    public UserData getUser(@PathVariable(value = "username") String username) {
        Optional<AppUser> optionalUser = userRepository.findById(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return new UserData(optionalUser.get());
    }

    @GetMapping("/api/user/{username}/permission")
    public QuestPermit permitQuest(@PathVariable(value = "username") String username){
        String permit = userRepository.findById(username).get().getPermit();
        return new QuestPermit("200", permit);
    }

    @DeleteMapping("/api/user/delete")
    public DeleteResponse deleteUser(@RequestParam(value = "username") String username){
        if(userRepository.existsById(username)){
            try{
                userRepository.deleteById(username);
                return new DeleteResponse("200", "Successfully delete");
            }catch (Exception e){
                return new DeleteResponse("400", e.getMessage());
            }

        }else{
            return new DeleteResponse("403", "User not found");
        }
    }

    @RequestMapping(value = "/avatar/{username}.png", method = RequestMethod.GET, produces = "image/png")
    public @ResponseBody byte[] genAvatar(@PathVariable(value = "username") String username)  {
        try {
            Avatar avatar = IdenticonAvatar.newAvatarBuilder().build();
            var img = avatar.create((username + "thu_summer_java_potato").hashCode());
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bao);
            return bao.toByteArray();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/user/register")
    public RedirectView register(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        if (userRepository.existsById(username))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        AppUser appUser = new AppUser(username, passwordEncoder.encode(password), authorities);
        userRepository.save(appUser);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(String.format("/api/user/login?username=%s&password=%s", username, password));
        return redirectView;
    }

    @PutMapping("/api/user/{username}/modify/info")
    public UserData modifyUser(@PathVariable(value = "username") String username, @RequestBody ModifyUserAttribute modifiedUser){
        Optional<AppUser> optionalUser = userRepository.findById(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        optionalUser.get().updateUserInfo(modifiedUser);
        userRepository.save(optionalUser.get());
        return new UserData(optionalUser.get());
    }

    @PutMapping("/api/user/{username}/modify/password")
    public ModifyPassResponse modifyPass(@PathVariable(value = "username") String username, @RequestBody ModifyPasswordAttribute modifiedUser){
        Optional<AppUser> optionalUser = userRepository.findById(username);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        if(optionalUser.get().getPassword() .equals(passwordEncoder.encode(modifiedUser.getOrigin()))){
            optionalUser.get().setPassword(passwordEncoder.encode(modifiedUser.getPassword()));
            userRepository.save(optionalUser.get());
            return new ModifyPassResponse("200", "修改密码成功");
        }

        userRepository.save(optionalUser.get());
        return new ModifyPassResponse("403","原密码不正确");
    }
}
