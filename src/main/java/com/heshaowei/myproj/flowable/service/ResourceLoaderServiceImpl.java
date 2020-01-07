package com.heshaowei.myproj.flowable.service;

import com.google.common.collect.Maps;
import com.heshaowei.myproj.flowable.bean.MyPrivilege;
import com.heshaowei.myproj.flowable.security.filters.resource.ResourceLoaderService;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceLoaderServiceImpl implements ResourceLoaderService {
    @Autowired
    private IdmIdentityService idmIdentityService;

    @Override
    public Map<String, List<? extends ConfigAttribute>> loadResources() {
        Map<String, List<? extends ConfigAttribute>> map = Maps.newHashMap();
        List<Privilege> privileges = idmIdentityService.createPrivilegeQuery().list();

        List<MyPrivilege> list = privileges.stream().map(privilege -> new MyPrivilege(privilege)).collect(Collectors.toList());
        map.put("/api/v1/user/list", list);
        return map;
    }
}
