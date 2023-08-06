package com.midtern.SpringCommerce.dto.request;

import com.midtern.SpringCommerce.constant.Role;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}
