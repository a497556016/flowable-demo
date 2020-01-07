package com.heshaowei.myproj.flowable.utils;

import com.heshaowei.myproj.flowable.bean.UserAccountInfo;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Optional;

public class UserUtil {
    private static final String USER_SESSION_KEY = "LoginUserInfo";

    private static ServletContext getServletContext() {
        Optional<WebApplicationContext> optional = Optional.ofNullable(SpringUtil.getApplicationContext());
        return optional.map(WebApplicationContext::getServletContext).orElse(null);
    }

    public static String getLoginUsername() {
        return Optional.ofNullable(getLoginUser()).map(UserAccountInfo::getUsername).orElse(null);
    }

    public static Integer getLoginUserId(){
        return Optional.ofNullable(getLoginUser()).map(UserAccountInfo::getId).orElse(null);
    }

    public static UserAccountInfo getLoginUser(){
        Object obj = Optional.ofNullable(getServletContext()).map(servletContext -> servletContext.getAttribute(USER_SESSION_KEY)).orElse(new UserAccountInfo());
        if(obj instanceof UserAccountInfo) {
            return (UserAccountInfo) obj;
        }
        return null;
    }
}
