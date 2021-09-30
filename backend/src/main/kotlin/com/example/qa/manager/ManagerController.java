package com.example.qa.manager;


import com.example.qa.manager.exchange.*;
import com.example.qa.manager.model.AppManager;
import com.example.qa.manager.repository.ManagerRepository;
import org.apache.catalina.Manager;
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
public class ManagerController {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/api/managers")
    public Iterable<AppManager> getManagers(){
        return managerRepository.findAll();
    }

    @GetMapping("/api/manager/{managername}")
    public ManagerData getManager(@PathVariable(value = "managername") String managername) {
        Optional<AppManager> optionalManager = managerRepository.findById(managername);
        if (optionalManager.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ManagerData(optionalManager.get());
    }
    @GetMapping("/api/manager/{managername}/permission")
    public QuestPermission permitQuest(@PathVariable(value = "managername") String managername){
        String permission = managerRepository.findById(managername).get().getPermission();
        return new QuestPermission("200", permission);
    }

    @DeleteMapping("/api/manager/delete")
    public DeleteResponse deleteManager(@RequestParam(value = "managername") String managername){
        if(managerRepository.existsById(managername)){
            try{
                managerRepository.deleteById(managername);
                return new DeleteResponse("200", "Successfully delete");
            }catch (Exception e){
                return new DeleteResponse("400", e.getMessage());
            }

        }else{
            return new DeleteResponse("403", "Manager not found");
        }
    }

    @PostMapping("/api/manager/create")
    public RedirectView register(@RequestParam(value = "managername") String managername, @RequestParam(value = "password") String password) {
        if (managerRepository.existsById(managername))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("manager"));
        AppManager appManager = new AppManager(managername, passwordEncoder.encode(password), authorities);
        managerRepository.save(appManager);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(String.format("/api/manager/login?managername=%s&password=%s", managername, password));
        return redirectView;
    }

    @PutMapping("/api/manager/{managername}/modify/info")
    public ManagerData modifyManager(@PathVariable(value = "managername") String managername, @RequestBody ModifyManagerAttribute modifiedManager){
        Optional<AppManager> optionalManager = managerRepository.findById(managername);
        if (optionalManager.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        optionalManager.get().updateManagerInfo(modifiedManager);
        managerRepository.save(optionalManager.get());
        return new ManagerData(optionalManager.get());
    }

    @PutMapping("/api/manager/{managername}/modify/password")
    public ModifyPassResponse modifyPass(@PathVariable(value = "managername") String managername, @RequestBody ModifyManagerPasswordAttribute modifiedManager){
        Optional<AppManager> optionalManager = managerRepository.findById(managername);
        if (optionalManager.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        if(optionalManager.get().getPassword() .equals(passwordEncoder.encode(modifiedManager.getOrigin_password()))){
            optionalManager.get().setPassword(passwordEncoder.encode(modifiedManager.getNew_password()));
            managerRepository.save(optionalManager.get());
            return new ModifyPassResponse("200", "修改密码成功");
        }

        managerRepository.save(optionalManager.get());
        return new ModifyPassResponse("400","原密码不正确");
    }



}