package com.heshaowei.myproj.flowable.controller;

import org.flowable.engine.IdentityService;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/user")
@RestController
public class UserAccountController {
    @Autowired
    private IdentityService identityService;

    @GetMapping("/list")
    public ResponseEntity list(){
        List<User> users = identityService.createUserQuery().list();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getByUsername")
    public ResponseEntity getByUsername(String username) {
        User user = identityService.createUserQuery().userId(username).singleResult();
        return ResponseEntity.ok(user);
    }
}
