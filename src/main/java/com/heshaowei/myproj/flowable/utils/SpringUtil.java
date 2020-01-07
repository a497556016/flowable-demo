package com.heshaowei.myproj.flowable.utils;

import org.flowable.engine.RuntimeService;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

public class SpringUtil {

    public static WebApplicationContext getApplicationContext(){
        return Optional.ofNullable(ContextLoader.getCurrentWebApplicationContext()).orElse(null);
    }

    public static RuntimeService getBean(Class<RuntimeService> clz) {
        return getApplicationContext().getBean(clz);
    }
}
