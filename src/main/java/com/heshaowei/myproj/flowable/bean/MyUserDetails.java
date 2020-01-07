package com.heshaowei.myproj.flowable.bean;

import lombok.Data;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MyUserDetails implements UserDetails {
    private String username;
    private String password;
    private Boolean locked = false;

    private List<? extends GrantedAuthority> authorities;

    public MyUserDetails(User user, List<Privilege> privileges) {
        if(null != user) {
            this.username = user.getId();
            this.password = user.getPassword();
        }
        if (null != privileges) {
            this.authorities = privileges.stream().map(privilege -> (GrantedAuthority) () -> privilege.getName()).collect(Collectors.toList());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
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
