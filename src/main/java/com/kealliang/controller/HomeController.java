package com.kealliang.controller;

import com.kealliang.base.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lsr
 * @ClassName HomeController
 * @Date 2019-01-30
 * @Desc 主页面
 * @Vertion 1.0
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "我是名字！");
        return "index";
    }

    @GetMapping("logout/page")
    public String logout(){
        return "logout";
    }

    @GetMapping("test")
    @ResponseBody
    public ApiResponse test() {
        return ApiResponse.ofSuccess("成功！");
    }

    @GetMapping("error/{code}")
    public String errorPage(@PathVariable Integer code) {
        return "error/" + code;
    }

    @GetMapping("testError")
    public void testError() {
        throw new NullPointerException("测试空指针异常");
    }
}
