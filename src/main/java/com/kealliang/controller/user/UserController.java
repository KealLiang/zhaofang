package com.kealliang.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lsr
 * @ClassName UserController
 * @Date 2019-02-01
 * @Desc
 * @Vertion 1.0
 */
@Controller
@RequestMapping("user")
public class UserController {

    @GetMapping("{page}")
    public String toPage(@PathVariable String page){
        return "user/" + page;
    }
}
