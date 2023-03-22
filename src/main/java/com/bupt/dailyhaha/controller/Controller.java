package com.bupt.dailyhaha.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
public class Controller {


    @RequestMapping("/today")
    public String today() {
        // redirect to today/index.html
        return "redirect:/today/index.html";
    }

    @RequestMapping("/admin")
    public String history() {
        // redirect to history/index.html
        return "redirect:/admin/index.html";
    }
}
