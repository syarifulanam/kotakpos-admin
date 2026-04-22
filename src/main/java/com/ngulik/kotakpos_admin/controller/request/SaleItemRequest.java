package com.ngulik.kotakpos_admin.controller.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaleItemRequest {
    private Long productId;
    private Long quantity;
}
