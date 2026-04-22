package com.ngulik.kotakpos_admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String icon;

    private String description;
}
