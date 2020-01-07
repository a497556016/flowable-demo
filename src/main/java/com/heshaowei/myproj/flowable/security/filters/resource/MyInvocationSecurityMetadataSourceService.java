package com.heshaowei.myproj.flowable.security.filters.resource;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 这里从数据库加载自定义的权限配置
 */
@Service
public class MyInvocationSecurityMetadataSourceService  implements
        FilterInvocationSecurityMetadataSource {

    @Autowired(required = false)
    private ResourceLoaderService resourceLoaderService;

    private Map<String, List<? extends ConfigAttribute>> map =null;

    /**
     * 加载权限表中配置的请求路径对应所需要的权限
     */
    public void loadResourceDefine(){
        map = new HashMap<>();

        if (null != resourceLoaderService) {
            map = resourceLoaderService.loadResources();
        }
    }

    //此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中，则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if(null == map) {
            loadResourceDefine();
        }

        FilterInvocation invocation = (FilterInvocation) object;
        //object 中包含用户请求的request 信息
        HttpServletRequest request = invocation.getHttpRequest();

        AntPathRequestMatcher matcher;
        String resUrl;
        for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            resUrl = iter.next();
            matcher = new AntPathRequestMatcher(resUrl);
            if(matcher.matches(request)) {
                return Lists.newArrayList(map.get(resUrl));
            }
        }

        //如果找不到权限配置信息，则默认不需要权限即可访问
        return Lists.newArrayList(new ConfigAttribute() {
            @Override
            public String getAttribute() {
                return "any";
            }
        });
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}