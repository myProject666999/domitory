package com.example.domitory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/user")
    public String user() {
        return "user/list";
    }
    
    @GetMapping("/building")
    public String building() {
        return "building/list";
    }
    
    @GetMapping("/room")
    public String room() {
        return "room/list";
    }
    
    @GetMapping("/repair")
    public String repair() {
        return "repair/list";
    }
    
    @GetMapping("/bill")
    public String bill() {
        return "bill/list";
    }
    
    @GetMapping("/account")
    public String account() {
        return "account/list";
    }
    
    @GetMapping("/allocation")
    public String allocation() {
        return "allocation/list";
    }
}
