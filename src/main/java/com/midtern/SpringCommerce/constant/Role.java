package com.midtern.SpringCommerce.constant;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.midtern.SpringCommerce.constant.Permission.*;

@Getter
public enum Role {
    ADMIN(Set.of(
            ADMIN_READ,
            ADMIN_WRITE,
            ADMIN_DELETE,
            Permission.USER_READ,
            Permission.USER_WRITE,
            Permission.USER_DELETE
    )),
    USER(Set.of(
            USER_READ,
            USER_WRITE,
            USER_DELETE
    ));

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getGrantedAuthorities() {
        List<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
