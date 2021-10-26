package com.lzb.mpmt.controller;

import com.lzb.mpmt.model.R;
import com.lzb.mpmt.model.User;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("userUserStaff")
public class UserUserStaffController {

    @RequestMapping("/list")
    public R<List<User>> list() {
        return new R();
    }
}
