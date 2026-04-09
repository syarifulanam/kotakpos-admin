package com.ngulik.kotakpos_admin.controller.response;

import com.ngulik.kotakpos_admin.enums.UserRole;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private long id;
    private String name;
    private String email;
    private UserRole role;
}
