package com.example.qa.config;

import org.springframework.web.bind.annotation.*;

import static com.example.qa.security.RestControllerAuthUtils.*;

@RestController
@RequestMapping("/api/config")
public class SystemConfigController {
    @GetMapping
    public Configurable getConfig() {
        return SystemConfig.getConfigurable();
    }

    @PutMapping
    public void updateConfig(@RequestBody Configurable configurable) {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        SystemConfig.updateConfig(configurable);
    }

    @GetMapping("/earnings")
    public EarningsResponse earnings() {
        authLoginOrThrow();
        authSuperAdminOrThrow();
        return new EarningsResponse(SystemConfig.getEarnings());
    }
}
