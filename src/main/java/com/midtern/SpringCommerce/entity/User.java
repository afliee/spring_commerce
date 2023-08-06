package com.midtern.SpringCommerce.entity;

import com.midtern.SpringCommerce.constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {
    @Column(unique = true)
    private String username;
    private String password;
    private Collection<Role> roles;
    private String avatar;
    @OneToOne(targetEntity = Cart.class)
    private Cart cart;

    @OneToMany(
            mappedBy = "user",
            targetEntity = Order.class
    )

    private Collection<Order> orders;

    @OneToMany(
            mappedBy = "user",
            targetEntity = Token.class
    )
    private Set<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role: roles) {
            authorities.addAll(role.getGrantedAuthorities());
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
