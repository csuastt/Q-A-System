package com.example.qa.admin.exchange;

import com.example.qa.admin.model.Admin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
public class AdminListResponse {
    private int pageSize;
    private int page;
    private int totalPages;
    private long totalCount;
    private AdminResponse[] data;

    public AdminListResponse(Page<Admin> adminPage) {
        pageSize = adminPage.getSize();
        page = adminPage.getNumber() + 1;
        totalPages = adminPage.getTotalPages();
        totalCount = adminPage.getTotalElements();
        data = adminPage.get().map(AdminResponse::new).toArray(AdminResponse[]::new);
    }
}
