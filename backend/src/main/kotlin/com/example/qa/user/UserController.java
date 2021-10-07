package com.example.qa.user;

import com.example.qa.user.exception.DeleteException;
import com.example.qa.user.exception.NotFoundException;
import com.example.qa.user.exception.NotMatchException;
import com.example.qa.user.exchange.*;
import com.example.qa.user.model.AppUser;
import com.example.qa.user.repository.UserRepository;
import com.talanlabs.avatargenerator.Avatar;
import com.talanlabs.avatargenerator.IdenticonAvatar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/api/users")
    public Iterable<AppUser> getUsers(){
        return userRepository.findAll();
    }



    @GetMapping("/api/users/{id}")
    public UserData getUser(@PathVariable(value = "id") Long id) {
        Optional<AppUser> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Not Found");
        return new UserData(optionalUser.get());
    }

    @GetMapping("/api/users/{id}/permission")
    public QuestPermit permitQuest(@PathVariable(value = "id") Long id){
        Optional<AppUser> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Not Found");
        String permit = optionalUser.get().getPermit();
        return new QuestPermit(permit);
    }

    @DeleteMapping("/api/users")
    public DeleteResponse deleteUser(@RequestParam(value = "id") Long id) throws DeleteException, NotFoundException {
        if(userRepository.existsById(id)){
            try{
                userRepository.deleteById(id);
                return new DeleteResponse("Successfully delete");
            }catch (Exception e){
                throw new DeleteException(e.getMessage());
            }

        }else{
            throw new NotFoundException("No Such Method");
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

    @PostMapping("/api/users")
    public RedirectView register(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        if (userRepository.existsByUsername(username))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        AppUser appUser = new AppUser(username, passwordEncoder.encode(password), authorities);
        userRepository.save(appUser);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(String.format("/api/user/login?username=%s&password=%s", username, password));
        return redirectView;
    }

    @PutMapping("/api/users/{id}")
    public HttpStatus modifyUser(@PathVariable(value = "id") Long id, @RequestBody ModifyUserAttribute modifiedUser){
        Optional<AppUser> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found");
        optionalUser.get().updateUserInfo(modifiedUser);
        userRepository.save(optionalUser.get());
        return HttpStatus.ACCEPTED;
    }

    @PutMapping("/api/users/{id}/password")
    public ModifyPassResponse modifyPass(@PathVariable(value = "id") Long id, @RequestBody ModifyPasswordAttribute modifiedUser) throws NotMatchException {
        Optional<AppUser> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if(passwordEncoder.matches(modifiedUser.getOrigin(),optionalUser.get().getPassword())){
            optionalUser.get().setPassword(passwordEncoder.encode(modifiedUser.getPassword()));
            userRepository.save(optionalUser.get());
            return new ModifyPassResponse("修改密码成功");
        }

        userRepository.save(optionalUser.get());
        throw new NotMatchException("原密码不正确");
    }
}
