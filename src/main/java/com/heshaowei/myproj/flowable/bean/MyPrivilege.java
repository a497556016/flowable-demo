package com.heshaowei.myproj.flowable.bean;

import org.flowable.idm.api.Privilege;
import org.flowable.idm.engine.impl.persistence.entity.PrivilegeEntityImpl;
import org.springframework.security.access.ConfigAttribute;

public class MyPrivilege extends PrivilegeEntityImpl implements ConfigAttribute {
    public MyPrivilege(Privilege privilege) {
        setName(privilege.getName());
        setId(privilege.getId());
    }


    @Override
    public String getAttribute() {
        return getName();
    }
}
