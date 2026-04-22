package com.ngulik.kotakpos_admin.mapper;

import com.ngulik.kotakpos_admin.dto.CategoryDto;
import com.ngulik.kotakpos_admin.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class categoryMapper {

    public CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .description(category.getDescription())
                .build();
    }
}
