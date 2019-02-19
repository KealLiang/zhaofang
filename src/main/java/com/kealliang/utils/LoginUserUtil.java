package com.kealliang.utils;

import com.kealliang.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author lsr
 * @ClassName LoginUserUtil
 * @Date 2019-02-03
 * @Desc 登录用户信息工具
 * @Vertion 1.0
 */
public class LoginUserUtil {

    public static User load() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    /** 
     * 获取登录用户id，找不到用户则返回-1
     * @author lsr
     * @description getLoginUserId
     * @Date 16:44 2019/2/3
     * @Param []
     * @return java.lang.Long
     */
    public static Long getLoginUserId() {
        User user = load();
        if (user == null) {
            return -1L;
        }
        return user.getId();
    }
}
