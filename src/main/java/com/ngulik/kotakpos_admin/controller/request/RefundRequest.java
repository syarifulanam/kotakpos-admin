package com.ngulik.kotakpos_admin.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundRequest {
    @NotBlank(message = "Reason is required")
    private String reason;
}
