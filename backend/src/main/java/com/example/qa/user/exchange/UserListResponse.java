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
    int pageSize;
    int page;
    int totalPages;
    UserResponse[] users;

    public UserListResponse(Page<User> userPage, int userResponseLevel) {
        pageSize = userPage.getSize();
        page = userPage.getNumber() + 1;
        totalPages = userPage.getTotalPages();
        users = userPage.get().map(user -> new UserResponse(user, userResponseLevel)).toArray(UserResponse[]::new);
    }
}
