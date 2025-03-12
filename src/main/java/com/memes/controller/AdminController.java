package com.memes.controller;

import com.memes.annotation.AuthRequired;
import com.memes.model.response.VisitStatistic;
import com.memes.service.impl.AdminService;
import com.memes.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor

public class AdminController {

    AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @AuthRequired
    @GetMapping("/validate")
    public boolean validate() {
        return true;
    }

    @AuthRequired
    @GetMapping("/statistic")
    public VisitStatistic statistic(@RequestParam(value = "date", required = false) String date) {
        if (date == null) {
            date = TimeUtil.getYMD();
        }
        return this.adminService.getVisitStatistic(date);
    }

}
