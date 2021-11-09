package com.example.qa.notification;

import com.example.qa.errorhandling.ApiException;
import com.example.qa.notification.exchange.NotifPayload;
import com.example.qa.notification.exchange.PagedList;
import com.example.qa.notification.model.Notification;
import com.example.qa.security.UserAuthentication;
import com.example.qa.user.UserRepository;
import com.example.qa.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
public class NotificationController {

    private final NotificationService notifService;
    private final NotificationRepository notifRepo;
    private final UserRepository userRepo;

    public NotificationController(
            @Autowired NotificationService notifService,
            @Autowired NotificationRepository notifRepo,
            @Autowired UserRepository userRepo) {
        this.notifService = notifService;
        this.notifRepo = notifRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/api/users/{userId}/notif")
    public PagedList<NotifPayload> getNotifications(
            @PathVariable long userId,
            @RequestParam Optional<Boolean> hasRead,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            Principal auth) {
        var user = checkUser(userId, auth);
        var pageable = PageRequest.of(page - 1, pageSize);
        return notifService.getNotifications(user, hasRead, pageable).map(NotifPayload::new);
    }

    @PostMapping("/api/users/{userId}/notif/{notifId}/read")
    @ResponseStatus(HttpStatus.OK)
    public void setRead(@PathVariable long userId, @PathVariable long notifId, Principal auth) {
        var user = checkUser(userId, auth);
        var notif = checkNotification(user, notifId);
        notifService.readOne(notif);
    }

    @PostMapping("/api/users/{userId}/notif/readAll")
    @ResponseStatus(HttpStatus.OK)
    public void readAll(@PathVariable long userId, Principal auth) {
        var user = checkUser(userId, auth);
        notifService.readAll(user);
    }

    @PostMapping("/api/users/{userId}/notif/deleteRead")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRead(@PathVariable long userId, Principal auth) {
        var user = checkUser(userId, auth);
        notifService.deleteRead(user);
    }

    private User checkUser(long userId, Principal auth) {
        if (auth instanceof UserAuthentication authUser) {
            if (userId != (long) authUser.getPrincipal()) {
                throw new ApiException(HttpStatus.FORBIDDEN);
            }
            return userRepo.findById(userId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND));
        } else {
            throw new ApiException(HttpStatus.UNAUTHORIZED);
        }
    }

    private Notification checkNotification(User user, long notifId) {
        var notif = notifRepo.findById(notifId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND));
        if (!notif.getReceiver().equals(user)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED);
        }
        return notif;
    }
}
