package com.midtern.SpringCommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
    private double total;
    private String address;
    private String address2;
    private String phone;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String cartQuantity;
}
