package com.heshaowei.myproj.flowable.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class UserUtil {

    public static String getLoginUsername() {
        return Optional.ofNullable(getLoginUser()).map(UserDetails::getUsername).orElse(null);
    }

    public static UserDetails getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object details = authentication.getPrincipal();
        if(details instanceof UserDetails) {
            return (UserDetails) details;
        }
        return null;
    }
}
