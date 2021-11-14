package com.example.qa.notification.exchange;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PagedList<T>(
        int pageSize,
        int page,
        int totalPages,
        long totalCount,
        List<T> data
) {
    public PagedList(Page<T> springPage) {
        this(springPage.getSize(), springPage.getNumber() + 1, springPage.getTotalPages(), springPage.getNumberOfElements(), springPage.toList());
    }

    public <U> PagedList<U> map(Function<T, U> mapper) {
        return new PagedList<>(
                pageSize, page, totalPages, totalCount,
                data.stream().map(mapper).toList()
        );
    }
}
