package com.ngulik.kotakpos_admin.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageHelper {

    public static Pageable defaultPageable(String sortDir, String sortBy, int page, int size) {
        if (sortBy.isBlank())
            sortBy = "id";

        if (size < 10)
            size = 10;

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }
}