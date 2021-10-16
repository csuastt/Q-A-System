package com.example.qa.manager;


import com.example.qa.errorhandling.ApiException;
import com.example.qa.manager.exception.DeleteException;
import com.example.qa.manager.exception.NotFoundException;
import com.example.qa.manager.exception.NotMatchException;
import com.example.qa.manager.exchange.*;
import com.example.qa.manager.model.AppManager;
import com.example.qa.manager.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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
    public Iterable<AppManager> getManagers() {
        return managerRepository.findAll();
    }


    @GetMapping("/api/managers/{id}")
    public ManagerData getManager(@PathVariable(value = "id") Long id) {
        Optional<AppManager> optionalManager = managerRepository.findById(id);

        if (optionalManager.isEmpty())
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ManagerData(optionalManager.get());
    }


    @GetMapping("/api/managers/{id}/permission")
    public QuestPermission permitQuest(@PathVariable(value = "id") Long id) {
        String permission = managerRepository.findById(id).get().getPermission();

        return new QuestPermission("200", permission);
    }


    @DeleteMapping("/api/managers")
    public DeleteResponse deleteUser(@RequestParam(value = "id") Long id) throws DeleteException, NotFoundException {
        if (managerRepository.existsById(id)) {

            try {
                managerRepository.deleteById(id);
                return new DeleteResponse("Successfully delete");
            } catch (Exception e) {

                throw new DeleteException(e.getMessage());
            }

        } else {

            throw new NotFoundException("No Such Method");
        }
    }

    @PostMapping("/api/managers")
    public RedirectView register(@RequestParam(value = "managername") String managername, @RequestParam(value = "password") String password) {
        if (managerRepository.existsByManagerName(managername))
            throw new ApiException(HttpStatus.FORBIDDEN);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("manager"));
        AppManager appManager = new AppManager(managername, passwordEncoder.encode(password), authorities);
        managerRepository.save(appManager);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(String.format("/api/manager/login?managername=%s&password=%s", managername, password));
        return redirectView;
    }


    @PutMapping("/api/managers/{id}")
    public ManagerData modifyUser(@PathVariable(value = "id") Long id, @RequestBody ModifyManagerAttribute modifiedManager) {
        Optional<AppManager> optionalManager = managerRepository.findById(id);

        if (optionalManager.isEmpty())
            throw new ApiException(HttpStatus.BAD_REQUEST);
        optionalManager.get().updateManagerInfo(modifiedManager);
        managerRepository.save(optionalManager.get());
        return new ManagerData(optionalManager.get());
    }


    @PutMapping("/api/managers/{id}/password")
    public ModifyPassResponse modifyPass(@PathVariable(value = "id") Long id, @RequestBody ModifyManagerPasswordAttribute modifiedManager) throws NotMatchException {
        Optional<AppManager> optionalManager = managerRepository.findById(id);

        if (optionalManager.isEmpty())
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR);
        if (optionalManager.get().getPassword().equals(passwordEncoder.encode(modifiedManager.getOrigin_password()))) {
            optionalManager.get().setPassword(passwordEncoder.encode(modifiedManager.getNew_password()));
            managerRepository.save(optionalManager.get());
            return new ModifyPassResponse("修改密码成功");
        }

        managerRepository.save(optionalManager.get());
        return new ModifyPassResponse("原密码不正确");
    }


}