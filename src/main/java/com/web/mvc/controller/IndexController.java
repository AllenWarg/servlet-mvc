package com.web.mvc.controller;


import com.web.mvc.bean.User;
import com.web.mvc.framework.ModelAndView;
import com.web.mvc.framework.annotation.Controller;
import com.web.mvc.framework.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class IndexController {

    @GetMapping("/")
    public ModelAndView index(HttpSession session) {
        System.out.println("IndexController首页");
        User user = (User) session.getAttribute("user");
        return new ModelAndView("/index.html", "user", user);
    }

    @GetMapping("/hello")
    public ModelAndView hello(String name) {
        if (name == null) {
            name = "World";
        }
        return new ModelAndView("/hello.html", "name", name);
    }
}
