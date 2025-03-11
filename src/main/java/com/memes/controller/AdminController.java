package com.memes.controller;

import com.memes.annotation.AuthRequired;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @AuthRequired
    @GetMapping("/validate")
    public boolean validate() {
        return true;
    }

}
