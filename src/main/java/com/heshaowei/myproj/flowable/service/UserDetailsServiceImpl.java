package com.heshaowei.myproj.flowable.service;

import com.heshaowei.myproj.flowable.bean.MyUserDetails;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private IdmIdentityService idmIdentityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = idmIdentityService.createUserQuery().userId(username).list();
        if(null == users || users.size() == 0) {
            throw new UsernameNotFoundException(String.format("用户名%s不存在！", username));
        }

        List<Privilege> privileges = idmIdentityService.createPrivilegeQuery().userId(username).list();

        return new MyUserDetails(users.get(0), privileges);
    }
}
