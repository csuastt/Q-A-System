package com.example.qa.config;

import com.example.qa.admin.AdminService;
import com.example.qa.admin.model.Admin;
import com.example.qa.exchange.EarningsResponse;
import com.example.qa.exchange.MonthlyEarnings;
import com.example.qa.order.OrderService;
import com.example.qa.order.model.Order;
import com.example.qa.user.UserService;
import com.example.qa.user.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.example.qa.security.RestControllerAuthUtils.*;

@RestController
@RequestMapping("/api/config")
public class SystemConfigController {

    private final UserService userService;
    private final OrderService orderService;
    private final AdminService adminService;

    public SystemConfigController(UserService userService, OrderService orderService, AdminService adminService) {
        this.userService = userService;
        this.orderService = orderService;
        this.adminService = adminService;
    }

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
        return new EarningsResponse(SystemConfig.getEarningsTotal(), MonthlyEarnings.toList(SystemConfig.getEarningsMonthly()));
    }

    @GetMapping("/stats")
    public SystemStatsResponse stats() {
        authLoginOrThrow();
        authAdminOrThrow();
        SystemStatsResponse response = new SystemStatsResponse();
        PageRequest pageRequest = PageRequest.of(0, 1);
        response.setUserCount(userService.listByRole(null, pageRequest).getTotalElements());
        response.setAnswererCount(userService.listByRole(List.of(User.Role.ANSWERER), pageRequest).getTotalElements());
        orderService.setPageRequest(pageRequest);
        response.setOrderCount(orderService.listByState(null).getTotalElements());
        response.setOrderToReviewCount(orderService.listByState(List.of(Order.State.CREATED)).getTotalElements());
        response.setPublicOrderCount(orderService.listByPublic(null).getTotalElements());
        response.setAdminCount(adminService.listByRole(Arrays.asList(Admin.Role.values()), pageRequest).getTotalElements());
        return response;
    }
}
