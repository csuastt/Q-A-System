package com.example.qa.user.exchange;

import com.example.qa.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
public class UserListResponse {
    private int pageSize;
    private int page;
    private int totalPages;
    private long totalUsers;
    private UserResponse[] users;

    public UserListResponse(Page<User> userPage, int userResponseLevel) {
        pageSize = userPage.getSize();
        page = userPage.getNumber() + 1;
        totalPages = userPage.getTotalPages();
        totalUsers = userPage.getTotalElements();
        users = userPage.get().map(user -> new UserResponse(user, userResponseLevel)).toArray(UserResponse[]::new);
    }
}
