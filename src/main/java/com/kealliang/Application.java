package com.kealliang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 
 * 启动app
 * @author lsr
 * @description 
 * @Date 15:05 2019/1/29
 * @Param 
 * @return 
 */
@SpringBootApplication
// SpringBoot2.0版本后安全配置不再可定制，需在此手动排除安全自动配置（或者需要时再添加security依赖）
//@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @GetMapping("hello")
    public String hello() {
        return "Hello, My House!";
    }

}

