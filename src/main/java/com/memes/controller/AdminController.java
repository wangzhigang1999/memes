package com.memes.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memes.annotation.AuthRequired;
import com.memes.model.response.VisitStatistic;
import com.memes.service.AdminService;
import com.memes.util.TimeUtil;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
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
    @GetMapping("/visit/statistic")
    public VisitStatistic visitStat(String date) {
        if (date == null) {
            date = TimeUtil.getYMD();
        }
        return this.adminService.getVisitStatistic(date);
    }

    @AuthRequired
    @GetMapping("/review/statistic")
    public Map<String, Long> reviewStat() {
        return this.adminService.getReviewStatistic();
    }
}
