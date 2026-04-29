package com.epam.gym.config.security;

import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Getter
@NullMarked
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final List<SimpleGrantedAuthority> roleList = new LinkedList<>();

    public CustomUserDetails(User user, List<UserRoleEnum> roles) {
        this.user = user;
        roles.forEach(role -> {
            this.roleList.add(new SimpleGrantedAuthority(role.name()));
        });

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getIsActive();
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